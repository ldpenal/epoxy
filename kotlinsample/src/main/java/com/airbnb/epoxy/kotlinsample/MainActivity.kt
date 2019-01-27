package com.airbnb.epoxy.kotlinsample

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.EpoxyTouchHelper
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.airbnb.epoxy.HierarchyExplorer
import com.airbnb.epoxy.kotlinsample.models.CarouselItemCustomViewModel_
import com.airbnb.epoxy.kotlinsample.models.ItemDataClass
import com.airbnb.epoxy.kotlinsample.models.ItemEpoxyHolder_
import com.airbnb.epoxy.kotlinsample.models.itemCustomView
import com.airbnb.epoxy.kotlinsample.models.itemEpoxyHolder
import com.airbnb.epoxy.kotlinsample.views.carouselNoSnap
import com.airbnb.epoxy.monitorFragments

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: EpoxyRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        recyclerView = findViewById(R.id.recycler_view)

        // Attach the visibility tracker to the RecyclerView. This will enable visibility events.
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(recyclerView)


        EpoxyTouchHelper.initSwiping(recyclerView)
            .leftAndRight() // Which directions to allow
            .withTargets(DataBindingItemBindingModel_::class.java, ItemEpoxyHolder_::class.java)
            .andCallbacks(object : EpoxyTouchHelper.SwipeCallbacks<EpoxyModel<*>?>() {
                override fun onSwipeCompleted(
                    model: EpoxyModel<*>?,
                    itemView: View?,
                    position: Int,
                    direction: Int
                ) {
                }
            })

        recyclerView.withModels {

            for (i in 0 until 100) {
                dataBindingItem {
                    id("data binding $i")
                    text("this is a data binding model: $i")
                    onClick { _ ->
                        Toast.makeText(this@MainActivity, "clicked", Toast.LENGTH_LONG).show()
                    }
                    onVisibilityStateChanged { model, view, visibilityState ->
                        Log.d(TAG, "$model -> $visibilityState")
                    }
                }

                itemCustomView {
                    id("custom view $i")
                    color(Color.GREEN)
                    title("this is a green custom view item: $i")
                    listener { _ ->
                        Toast.makeText(this@MainActivity, "clicked", Toast.LENGTH_LONG).show()
                    }
                }

                itemEpoxyHolder {
                    id("view holder $i")
                    title("this is a View Holder item: $i")
                    listener {
                        Toast.makeText(this@MainActivity, "clicked", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                carouselNoSnap {
                    id("carousel $i")
                    models(mutableListOf<CarouselItemCustomViewModel_>().apply {
                        val lastPage = 10
                        for (j in 0 until lastPage) {
                            add(
                                CarouselItemCustomViewModel_()
                                    .id("carousel $i-$j")
                                    .title("Carousel: $i, Page $j / $lastPage")
                            )
                        }
                    })
                }

                // Since data classes do not use code generation, there's no extension generated here
                ItemDataClass("this is a Data Class Item: $i")
                    .id("data class $i")
                    .addTo(this)
            }
        }

        recyclerView.post {

            HierarchyExplorer(
                root = recyclerView.parent as View,
                onEnd = {
                    log("ended")
                },
                viewCallback = HierarchyExplorer.ViewCallback(
                    onView = {
                        log("View: ${it.view::class.java.simpleName} : ${it.branches}")
//                        recyclerView.post { it.hierarchyExplorer.resumeFromLastView() }
                        true
                    },
                    onClickableView = {
                        log("Clickable view: ${it.view::class.java.simpleName} : ${it.branches}")
//                        recyclerView.post { it.hierarchyExplorer.resumeFromLastView() }
                        true
                    },
                    onLongClickableView = {
                        log("Long clickable view: ${it.view::class.java.simpleName} : ${it.branches}")
//                        recyclerView.post { it.hierarchyExplorer.resumeFromLastView() }
                        true
                    }
                )
            ).start()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

fun log(msg: String) = Log.d("Eli test", msg)

fun EpoxyRecyclerView.withModels(buildModelsCallback: EpoxyController.() -> Unit) {
    setControllerAndBuildModels(object : EpoxyController() {
        override fun buildModels() {
            buildModelsCallback()
        }
    })
}
