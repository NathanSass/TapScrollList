package com.example.tapscrolllist

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = "abcdefghijklmnopqrstuvwxyz".split("").toTypedArray()
        viewAdapter = MyAdapter(data)

        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            val viewManager = LinearLayoutManager(context)
            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

            addOnItemTouchListener(TapToScrollTouchInterceptor(viewManager, this))
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    Log.v(TAG, "onScrolled")

                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    Log.v(TAG, "onScrollStateChanged")
                }
            })

        }
    }
}

class TapToScrollTouchInterceptor(private val layoutManager: LinearLayoutManager, private val recyclerView: RecyclerView) : RecyclerView.OnItemTouchListener {

    private companion object {
        const val TAG = "TapToScrollInterceptor"
        private const val SWIPE_THRESHOLD = 100
    }

    private var firstEvent: Float? = null

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean = true

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        if (e.action == MotionEvent.ACTION_MOVE && firstEvent == null) {
            firstEvent = e.y
        } else if (e.action == MotionEvent.ACTION_UP && firstEvent != null) {
            calculateSwipeDirection(firstEvent!!, e.y)
            firstEvent = null
        }
    }

    private fun calculateSwipeDirection(yStart: Float, yEnd: Float) {
        val diffY = yEnd - yStart
        if (abs(diffY) > SWIPE_THRESHOLD) {
            if (diffY > 0) {
                swipeUp()
            } else {
                swipeDown()
            }
        } else {
            Log.v(TAG, "Swipe too small")
        }

    }

    private fun swipeDown() {
        val lastItemVisiblePosition = layoutManager.findLastVisibleItemPosition()
        layoutManager.scrollToPositionWithOffset(lastItemVisiblePosition - 1, 0) // neg 1 ruins large views
    }

    private fun swipeUp() {
        val firstItemVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val height = layoutManager.height - recyclerView[0].height
        layoutManager.scrollToPositionWithOffset(firstItemVisiblePosition, height)
    }

}
