package ss.anoop.awesomenavigation.internal.navigations

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import ss.anoop.awesomenavigation.internal.NavigationDelegate
import ss.anoop.awesomenavigation.internal.delegates.ConfigDelegate
import ss.anoop.awesomenavigation.internal.delegates.DataDelegate
import ss.anoop.awesomenavigation.internal.delegates.ListenerDelegate
import ss.anoop.awesomenavigation.internal.delegates.ViewDelegate
import ss.anoop.awesomenavigation.internal.extensions.asRect

class NavigationStyle2(
    override val viewDelegate: ViewDelegate,
    override val dataDelegate: DataDelegate,
    override val configDelegate: ConfigDelegate,
    override val listenerDelegate: ListenerDelegate
) : NavigationDelegate, View.OnTouchListener {

    private val iconRects = mutableListOf<RectF>()

    private var itemTextWidths = mutableListOf<Int>()

    private val textPaint = Paint(ANTI_ALIAS_FLAG)

    private val selectionPaint = Paint(ANTI_ALIAS_FLAG)

    private var selectedIndex = 0

    private val selectionRect = RectF()

    private var selectionRadius = 0f

    init {
        viewDelegate.setTouchListener(this)
    }

    override fun onSizeChanged() {
        val padding = viewDelegate.width
            .minus(
                configDelegate.iconSize.times(
                    dataDelegate.menuItems.size
                )
            ).minus(
                configDelegate.itemSpacing.times(
                    dataDelegate.menuItems.size.minus(1)
                )
            ).div(2)

        val top = viewDelegate.height.minus(
            configDelegate.iconSize
        ).div(2)
        var rectF = RectF(
            padding,
            top,
            padding.plus(
                configDelegate.iconSize
            ),
            top.plus(
                configDelegate.iconSize
            )
        )


        textPaint.textSize = configDelegate.textSize
        val textBounds = Rect()

        dataDelegate.menuItems.forEachIndexed { index, menuItem ->
            if (index > 0) {
                rectF = RectF(
                    rectF.right.plus(configDelegate.itemSpacing),
                    rectF.top,
                    rectF.right.plus(configDelegate.itemSpacing).plus(
                        configDelegate.iconSize
                    ),
                    rectF.bottom
                )
            }
            iconRects.add(
                rectF
            )

            textPaint.getTextBounds(menuItem.title, 0, menuItem.title.length, textBounds)
            itemTextWidths.add(textBounds.width())
        }

        expand(0)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(
            selectionRect,
            selectionRadius,
            selectionRadius,
            selectionPaint
        )
        dataDelegate.menuItems.forEachIndexed { index, menuItem ->
            with(menuItem.icon) {
                bounds = iconRects[index].asRect()
                draw(canvas)
            }

        }
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_UP) {
            iconRects.forEachIndexed { index, rectF ->
                if (rectF.contains(event.x, event.y)) {
                    onSelectItem(index)
                }
            }
        }
        return true
    }

    override fun selectItem(position: Int) {
        onSelectItem(position)
    }

    private fun onSelectItem(position: Int) {
        if (selectedIndex == position) {
            listenerDelegate.navigationListener
                ?.onReselectNavigation(
                    dataDelegate.menuItems[position].id,
                    position
                )
        }
    }

    private fun expand(index: Int) {
        selectionPaint.color = Color.parseColor("#99ff1100")
        with(iconRects[index]) {
            selectionRect.left = left.minus(configDelegate.iconSpacing)
            selectionRect.top = top.minus(configDelegate.iconSpacing)
            selectionRect.right = right.plus(configDelegate.iconSpacing)
            selectionRect.bottom = bottom.plus(configDelegate.iconSpacing)
        }
        viewDelegate.invalidate()

        selectionRadius = selectionRect.height().div(2)

        ValueAnimator.ofFloat(
            0f,
            configDelegate.iconSpacing.times(2).plus(
                itemTextWidths[index]
            )
        ).apply {
            duration = configDelegate.animationDuration
            interpolator = LinearInterpolator()
            addUpdateListener { onSelectionAnimationUpdate(it, index) }
        }.also {
            it.start()
        }
    }

    private fun onSelectionAnimationUpdate(animator: ValueAnimator, index: Int) {
        selectionRect.right = selectionRect.left.plus(
            configDelegate.iconSize
        ).plus(configDelegate.iconSpacing.times(2))
            .plus(animator.animatedValue as Float)

        for(i in 1 until dataDelegate.menuItems.size){
            iconRects[i].apply {

            }
        }
        viewDelegate.invalidate()
    }
}