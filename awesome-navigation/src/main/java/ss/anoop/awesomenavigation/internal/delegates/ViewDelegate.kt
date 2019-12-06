package ss.anoop.awesomenavigation.internal.delegates

import android.view.View

interface ViewDelegate {

    val width: Float

    val height: Float

    fun setTouchListener(onTouchListener: View.OnTouchListener)

    fun invalidate()
}