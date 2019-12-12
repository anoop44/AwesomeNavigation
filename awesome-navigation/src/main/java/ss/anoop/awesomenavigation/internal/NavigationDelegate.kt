package ss.anoop.awesomenavigation.internal

import android.graphics.Canvas
import ss.anoop.awesomenavigation.internal.delegates.ConfigDelegate
import ss.anoop.awesomenavigation.internal.delegates.DataDelegate
import ss.anoop.awesomenavigation.internal.delegates.ListenerDelegate
import ss.anoop.awesomenavigation.internal.delegates.ViewDelegate

internal interface NavigationDelegate {

    val viewDelegate: ViewDelegate

    val dataDelegate: DataDelegate

    val configDelegate: ConfigDelegate

    val listenerDelegate: ListenerDelegate

    fun draw(canvas: Canvas)

    fun onSizeChanged()

    fun selectItem(position: Int)
}