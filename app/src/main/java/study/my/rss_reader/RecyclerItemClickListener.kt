package study.my.rss_reader

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class RecyclerItemClickListener(recyclerView: RecyclerView, private val mListener: OnItemClickListener?) : RecyclerView.OnItemTouchListener {
    private var mGestureDetector: GestureDetector = GestureDetector(recyclerView.context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return true
        }

        override fun onLongPress(e: MotionEvent) {}
    })

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
        fun onLongItemClick(child: View, position: Int)
    }

    override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
        val childView = view.findChildViewUnder(e.x, e.y)
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView))
            return true
        }
        return false
    }
    inline fun RecyclerView.setOnItemClickListener(crossinline listener: (position: Int) -> Unit) {
        addOnItemTouchListener(RecyclerItemClickListener(this,
            object : RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    listener(position)
                }

                override fun onLongItemClick(child: View, position: Int) {
                    listener(position)
                }
            }))
    }


    override fun onTouchEvent(view: RecyclerView, motionEvent: MotionEvent) {}
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}