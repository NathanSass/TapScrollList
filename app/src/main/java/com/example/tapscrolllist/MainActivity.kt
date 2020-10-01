package com.example.tapscrolllist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
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

        val viewManager = LinearLayoutManager(this)
        val data = "abcdefghijklmnopqrstuvwxyz".split("").toTypedArray()
        viewAdapter = MyAdapter(data)

        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

            addOnItemTouchListener(CoolTouchInterceptor())
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

class CoolTouchInterceptor : RecyclerView.OnItemTouchListener {

    var firstEvent: Float? = null

    val TAG = "TOUCH_INTERCEPTOR"
    private val SWIPE_THRESHOLD = 100;
    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        if (e.action == MotionEvent.ACTION_MOVE && firstEvent == null) {
            firstEvent = e.y
        } else if (e.action == MotionEvent.ACTION_UP && firstEvent != null) {
            calculateSwipeDirection(firstEvent!!, e.y)
            firstEvent = null
        }

        //        val layoutManager = rv.layoutManager as LinearLayoutManager
    }

    private fun calculateSwipeDirection(yStart: Float, yEnd: Float) {
        val diffY = yEnd - yStart
        if (abs(diffY) > SWIPE_THRESHOLD) {
            if (diffY > 0) {
                Log.v(TAG, "Swipe DOWN")
            } else {
                Log.v(TAG, "Swipe UP")
            }

        } else {
            Log.v(TAG, "Swipe too small")
        }

    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean = true

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit
}

class CoolLayoutManager(context: Context) : LinearLayoutManager(context) {
    override fun canScrollVertically(): Boolean = false
}