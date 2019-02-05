package com.airbnb.epoxy

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.LinkedList

class HierarchyExplorer(
    private val root: View,
    private val onEnd: () -> Unit,
    private val viewCallback: ViewCallback,
    resumeFromState: State? = null
) {
    private val viewChain = mutableListOf<View>()
    private val branchChain = LinkedList<Int>()
    private val resumingChain = LinkedList<Int>()

    private class Action(
        val id: Int,
        val callback: (ViewDetails) -> Boolean,
        val qualifier: (View) -> Boolean
    )

    private val actions = listOf(
        Action(1, viewCallback.onView) { true },
        Action(2, viewCallback.onClickableView) { it.isClickable },
        Action(3, viewCallback.onLongClickableView) { it.isLongClickable }
    )
    private val actionsTakenOnCurrentView = mutableSetOf<Int>()

    open class ViewCallback(
        val onView: (view: ViewDetails) -> Boolean = { true },
        val onClickableView: (view: ViewDetails) -> Boolean = { true },
        val onLongClickableView: (view: ViewDetails) -> Boolean = { true },
        val onRecyclerViewScrolled: (recyclerView: RecyclerView, position: Int) -> Boolean = { _, _ -> true }
    )

    data class State(
        val branchChain: List<Int>,
        val actionsTakenOnCurrentView: Set<Int>
    )

    class ViewDetails(
        val hierarchyExplorer: HierarchyExplorer,
        val view: View,
        val parentHierarchy: List<View>,
        val branches: List<Int>
    )

    init {
        resumeFromState?.let {
            branchChain.addAll(it.branchChain)
            actionsTakenOnCurrentView.addAll(it.actionsTakenOnCurrentView)
        }
    }

    fun resume() {
        viewChain.clear()
        resumingChain.clear()
        resumingChain.addAll(branchChain)
        iterate(root)
    }

    val state: State
        get() = State(
            branchChain.toList(),
            actionsTakenOnCurrentView.toSet()
        )

    private fun iterate(view: View): Boolean {
        val isResuming = resumingChain.isNotEmpty()

        if (!isResuming) {
            actions
                .filter { it.qualifier(view) }
                .filterNot { actionsTakenOnCurrentView.contains(it.id) }
                .forEach { action ->
                    actionsTakenOnCurrentView.add(action.id)

                    if (!action.callback(
                            ViewDetails(
                                this,
                                view,
                                viewChain,
                                branchChain
                            )
                        )
                    ) {
                        return false
                    }
                }

            // Finished processing actions on this view, moving on to children
            actionsTakenOnCurrentView.clear()
        }

        viewChain.add(view)

        // Processing view children
        if (view is RecyclerView) {
            if (!iterateRecyclerView(view)) {
                return false
            }
        } else if (view is ViewGroup) {
            val resumingIndex = resumingChain.pollFirst()

            for (index in (resumingIndex ?: 0) until view.childCount) {
                if (resumingIndex != index) {
                    branchChain.addLast(index)
                }
                if (!iterate(view.getChildAt(index))) {
                    return false
                }
                branchChain.pollLast()
            }
        }

        // Finished with children, moving back up to this view's parent
        viewChain.remove(view)
        if (view == root) {
            onEnd()
        }
        return true
    }

    private fun iterateRecyclerView(
        recyclerView: RecyclerView
    ): Boolean {
        val itemCount = recyclerView.adapter?.itemCount ?: return true

        val resumingIndex = resumingChain.pollFirst()
        ((resumingIndex ?: 0) until itemCount).forEach { targetItemIndex ->

            if (resumingIndex != targetItemIndex) {
                branchChain.addLast(targetItemIndex)
            }

            recyclerView
                .findViewHolderForAdapterPosition(targetItemIndex)
                ?.itemView
                ?.let {

                    if (!iterate(it)) {
                        return false
                    }
                    branchChain.pollLast()
                    return@forEach
                }

            // Use scrollToPositionWithOffset if possible so that the view is brought to the very top.
            // this prevents less frequent scrolling needed for future items
            (recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                targetItemIndex,
                0
            )
                ?: recyclerView.scrollToPosition(targetItemIndex)

            if (viewCallback.onRecyclerViewScrolled(recyclerView, targetItemIndex)) {
                recyclerView.post {
                    resume()
                }
            }

            return false
        }

        return true
    }
}


// ViewModel method called - can we get the method name? maybe looking at the stacktrace when the new state is set
// leads to new view model state we can intercept

// Activity setResult - final so can't override. could check values reflectively after each click and check for changes
// Set some local var - shouldn't be allowed with mvrx

// Show snackbar - could catch with our SnackbarWrapper. Captured by global layout listener? handler in registerFailurePoptarts?
// Show toast - could force toast wrapper usage. not important, toasts shouldn't be used.
// Log event to Airevents/jitney

// Call random thing on class injected into fragment (account manager, wishlist manager, etc)
// Mock out all injected things?

// Call random methods on fragment/activity like setting transition callback or listeners

// Change toolbar theming, or touch views directly in other ways, play lottie animation, expand view, etc

// Handled:
// start activity - can override functions in activity, startActivity, startIntentSender, startActivityFromFragment
// finish activity - override finish and related methods
// Navigate up - navigateUpTo, onNavigateUp
// activity onBackPressed
// on home pressed - AirActivity#onHomeActionPressed or check option item selected item.getItemId() == android.R.id.home