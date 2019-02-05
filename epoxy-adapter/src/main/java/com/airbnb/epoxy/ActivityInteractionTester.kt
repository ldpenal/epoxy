package com.airbnb.epoxy

import android.os.Build
import android.os.Looper
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class ActivityInteractionTester(
    val activity: FragmentActivity,
    val resetViewCallback: () -> Unit,
    val entryPointProvider: () -> View,
    val interactionReporters: List<InteractionManager.InteractionReporter>,
    val onEnd: () -> Unit
) : LifecycleObserver {

    private var savedHierarchyState: HierarchyExplorer.State? = null
    private val interactionManager = InteractionManager()

    init {
        activity.lifecycle.addObserver(this)
        interactionReporters.forEach { it.interactionManager = interactionManager }
    }

    fun resume() {
        val stateToResume = savedHierarchyState
        savedHierarchyState = null
        HierarchyExplorer(
            root = entryPointProvider(),
            onEnd = {
                interactionReporters.forEach { it.interactionManager = null }
                onEnd()
            },
            viewCallback = hierarchyExplorerCallbacks,
            resumeFromState = stateToResume
        ).resume()
    }

    private val hierarchyExplorerCallbacks = HierarchyExplorer.ViewCallback(
        onClickableView = {
            handleAction("click", it) { performClick() }
        },
        onLongClickableView = {
            handleAction("longClick", it) { performLongClick() }
        },
        onRecyclerViewScrolled = { recyclerView, position ->
            true
        }
    )

    private fun handleAction(
        actionName: String,
        viewDetails: HierarchyExplorer.ViewDetails,
        action: View.() -> Unit
    ): Boolean {
        interactionManager.onInteractionStarted(actionName, viewDetails)

        // Potentially catch exceptions.
        // Some might be allowable.
        // In either case, saving exceptions for final report allows multiple issues to be caught
        // in one pass instead of needing to fix one at a time
        viewDetails.view.action()


        onIdle {
            // Potentially take screenshot and record hash?
            // Check PendingIntent started?
            // check activity result set? needs reflection on activity I think

            interactionManager.onInteractionEnded()

            // Resetting view, maybe not necessary if no actions happened?
            savedHierarchyState = viewDetails.hierarchyExplorer.state
            resetViewCallback()
        }

        return false
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onActivityLifecycleEvent() {
        // We don't know how to recover from the activity changing :/
        throw IllegalStateException("Activity lifecycle change triggered during interaction test: ${activity.lifecycle.currentState}")
    }

    private fun onIdle(callback: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // looper.getQueue was added in API 23
            val mainThreadQueue = Looper.getMainLooper().queue
            if (mainThreadQueue.isIdle) {
                callback()
            } else {
                mainThreadQueue.addIdleHandler {
                    callback()
                    return@addIdleHandler false // Removes the idle handler
                }
            }
        } else {
            // Not ideal
            activity.window.decorView.post { callback() }
        }
    }
}

class InteractionManager {

    private var currentViewAction: ViewAction? = null
    private val reportedInteractions = mutableListOf<ReportedInteraction>()
    private val results = LinkedHashMap<ViewId, MutableList<ActionResult>>()

    private data class ViewId(
        val viewName: String,
        val customComponentChain: String?,
        val branching: String
        // TODO Look up id resource name?
        // TODO Epoxy model?
    )

    private class ActionResult(
        val actionName: String,
        val interactions: List<ReportedInteraction>
    )

    private class ViewAction(
        val actionName: String,
        val viewDetails: HierarchyExplorer.ViewDetails
    )

    private class ReportedInteraction(
        val reporterName: String,
        val interactionName: String,
        val details: String
    )

    internal fun generateReport(): JSONObject {
        return results.keys.fold(JSONObject()) { json, key ->
            val viewActions = results[key]!!

            json.put(key.branching, JSONObject().apply {
                put("view", key.viewName)
                put("hierarchy", key.customComponentChain)
                put("actions", JSONObject().apply {
                    viewActions.forEach { actionResult ->
                        put(actionResult.actionName, JSONArray().apply {
                            actionResult.interactions.forEach {
                                put(JSONObject().apply {
                                    put("reporter", it.reporterName)
                                    put("interaction", it.interactionName)
                                    put("details", it.details)
                                })
                            }
                        })
                    }
                })
            })
        }
    }

    internal fun onInteractionStarted(
        actionName: String,
        viewDetails: HierarchyExplorer.ViewDetails
    ) {
        require(currentViewAction == null) { "Interaction is already ongoing" }
        currentViewAction = ViewAction(actionName, viewDetails)
    }

    internal fun onInteractionEnded() {
        val viewAction = currentViewAction
            ?: throw java.lang.IllegalStateException("Interaction was not started")

        val viewId = ViewId(
            viewName = viewAction.viewDetails.view::class.java.simpleName,
            customComponentChain = viewAction.buildParentChain(),
            branching = viewAction.viewDetails.branches.joinToString()
        )

        val viewActionResults = results.getOrPut(viewId) { mutableListOf() }
        viewActionResults.add(ActionResult(viewAction.actionName, reportedInteractions))

        currentViewAction = null
        reportedInteractions.clear()
    }

    private fun ViewAction.buildParentChain() =
        viewDetails.parentHierarchy
            .filter {
                it::class.java.simpleName == RecyclerView::class.java.simpleName ||
                    it::class.java.canonicalName?.startsWith("android.") != true
            }
            .map { it::class.java.simpleName }
            .joinToString(separator = " -> ")

    fun reportInteraction(
        reporter: InteractionReporter,
        interactionName: String,
        details: String,
        duplicateStrategy: InteractionReporter.DuplicateStrategy
    ) {
        val interaction =
            ReportedInteraction(reporter::class.java.simpleName, interactionName, details)

        if (duplicateStrategy != InteractionReporter.DuplicateStrategy.KeepAll) {
            reportedInteractions
                .indexOfFirst {
                    it.interactionName == interactionName && it.reporterName == interaction.reporterName
                }
                .takeIf { it != -1 }
                ?.let { interactionIndex ->

                    when (duplicateStrategy) {
                        InteractionReporter.DuplicateStrategy.KeepFirst -> return
                        InteractionReporter.DuplicateStrategy.KeepLast -> {
                            reportedInteractions.removeAt(interactionIndex)
                        }
                        InteractionReporter.DuplicateStrategy.KeepAll -> {
                        }
                    }

                }
        }

        reportedInteractions.add(interaction)
    }

    interface InteractionReporter {

        enum class DuplicateStrategy {
            KeepAll,
            KeepFirst,
            KeepLast
        }

        var interactionManager: InteractionManager?

        fun reportInteraction(
            interactionName: String,
            details: String,
            duplicateStrategy: DuplicateStrategy = DuplicateStrategy.KeepAll
        ) {
            interactionManager?.reportInteraction(
                this,
                interactionName,
                details,
                duplicateStrategy
            )
        }
    }
}


