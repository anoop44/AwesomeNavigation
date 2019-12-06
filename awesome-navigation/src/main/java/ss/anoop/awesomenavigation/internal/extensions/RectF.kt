package ss.anoop.awesomenavigation.internal.extensions

import android.graphics.Rect
import android.graphics.RectF

internal fun RectF.asRect(): Rect {
    val rect = Rect()
    this.round(rect)
    return rect
}

internal fun RectF.copy(
    newLeft: Float = left,
    newTop: Float = top,
    newRight: Float = right,
    newBottom: Float = bottom
) = RectF(
    newLeft,
    newTop,
    newRight,
    newBottom
)