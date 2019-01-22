package com.airbnb.epoxy.kotlinsample

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView

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

class HierarchyExplorer {
    private val viewChain = mutableListOf<View>()

    // Could register fragment lifecycle observer to catch unexpected changes

    // Register listener for logging and get logs on fragment start/end, and clicks

    fun iterate(view: View) {
        view.viewTreeObserver.addOnGlobalLayoutListener {
            // unknown view change detected - screenshot? reset fragment state?
        }

        (view.context as? FragmentActivity)?.lifecycle?.addObserver(ActivityLifeCycleObserver())
        viewChain.add(view)


        if (view.isClickable) {
            view.performClick()
            recordAction()
            // If something unexpected happened (like view change) we could reset the fragment and start again
        }
        if (view.isLongClickable) {
            view.performLongClick()
            recordAction()
        }

        if (view is RecyclerView) {
            iterateRecyclerView(view)
        } else if (view is ViewGroup) {
            for (index in 0 until view.childCount) {
                iterate(view.getChildAt(index))
            }
        }
    }

    private fun iterateRecyclerView(recyclerView: RecyclerView, startingPosition: Int = 0) {
        val itemCount = recyclerView.adapter?.itemCount ?: return
        (0 until itemCount).forEach { targetItemIndex ->
            for (childViewIndex in 0 until recyclerView.childCount) {
                val child = recyclerView.getChildAt(childViewIndex)
                if (recyclerView.getChildAdapterPosition(child) == targetItemIndex) {
                    iterate(child)
                    return@forEach
                }
            }

            recyclerView.scrollToPosition(targetItemIndex)
            recyclerView.post {
                iterateRecyclerView(recyclerView, targetItemIndex)
            }
            return
        }
    }

    private fun recordAction() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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