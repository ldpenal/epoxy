package com.airbnb.epoxy

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.LinkedList

fun FragmentActivity.monitorFragments() {
    fun Fragment.log(lifecycleCallback: String, fm: FragmentManager) {
        val nestedFragment = fm != supportFragmentManager
        println("Fragment event: name:${this::class.java.simpleName} callback:$lifecycleCallback nested:$nestedFragment")
        // Return, revert fragment transaction, and continue iterating views
    }

    supportFragmentManager.registerFragmentLifecycleCallbacks(object :
        FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
            f.log("Attached", fm)
        }

        override fun onFragmentCreated(
            fm: FragmentManager,
            f: Fragment,
            savedInstanceState: Bundle?
        ) {
            f.log("Created", fm)
        }

        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            f.log("Started", fm)
        }

        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            f.log("Resumed", fm)
        }

        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
            f.log("Paused", fm)
        }

        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            f.log("Stopped", fm)
        }

        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            f.log("Destroyed", fm)
        }

        override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
            f.log("ViewDestroyed", fm)
        }

        override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
            f.log("Detached", fm)
        }
    }, true)
}

class HierarchyExplorer(
    val root: View,
    val onEnd: () -> Unit,
    viewCallback: ViewCallback
) {
    private val viewChain = mutableListOf<View>()
    private val branchChain = LinkedList<Int>()
    private val resumingChain = LinkedList<Int>()

    private class Action(
        val callback: (ViewDetails) -> Boolean,
        val qualifier: (View) -> Boolean
    )

    private val actions = listOf(
        Action(viewCallback.onView) { true },
        Action(viewCallback.onClickableView) { it.isClickable },
        Action(viewCallback.onLongClickableView) { it.isLongClickable }
    )
    private val actionsTakenOnCurrentView = mutableSetOf<(ViewDetails) -> Boolean>()

    open class ViewCallback(
        val onView: (view: ViewDetails) -> Boolean = { true },
        val onClickableView: (view: ViewDetails) -> Boolean = { true },
        val onLongClickableView: (view: ViewDetails) -> Boolean = { true }
    )

    class ViewDetails(
        val hierarchyExplorer: HierarchyExplorer,
        val view: View,
        val parentHierarchy: List<View>,
        val branches: List<Int>
    )

    // Could register fragment lifecycle observer to catch unexpected changes

    // Register listener for logging and get logs on fragment start/end, and clicks

    fun start() {
        root.viewTreeObserver.addOnGlobalLayoutListener {
            // unknown view change detected - screenshot? reset fragment state?
        }

        (root.context as? FragmentActivity)?.lifecycle?.addObserver(ActivityLifeCycleObserver())

        iterate(root)
    }

    private fun iterate(view: View): Boolean {
        val isResuming = resumingChain.isNotEmpty()

        if (!isResuming) {
            actions
                .filter { it.qualifier(view) }
                .map { it.callback }
                .filterNot { actionsTakenOnCurrentView.contains(it) }
                .forEach { callback ->
                    actionsTakenOnCurrentView.add(callback)

                    if (!callback(
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
            viewChain.add(view)
        }

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
            //        TODO("remove layout listener")
        }
        return true
    }

    fun resumeFromLastView() {
        resumingChain.clear()
        resumingChain.addAll(branchChain)
        iterate(root)
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

            recyclerView.post {
                resumeFromLastView()
            }

            return false
        }

        return true
    }

    inner class ActivityLifeCycleObserver : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
        fun onLifecycleEvent() {
            // Error case, we must have missed something
        }
    }
}

// Fragment manager calls - could override activity's get fragmentmanager methods and return a wrapped fragment manager that tracks calls
// That doesn't work for fragments, since those methods are final
// Force all airbnb fragment calls to go through an object we provide?
//

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

// Change toolbar theming, or touch views directly in other ways

// Handled:
// start activity - can override functions in activity, startActivity, startIntentSender, startActivityFromFragment
// finish activity - override finish and related methods
// Navigate up - navigateUpTo, onNavigateUp
// activity onBackPressed
// on home pressed - AirActivity#onHomeActionPressed or check option item selected item.getItemId() == android.R.id.home