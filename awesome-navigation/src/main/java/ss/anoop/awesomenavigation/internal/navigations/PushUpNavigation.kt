package ss.anoop.awesomenavigation.internal.navigations

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.text.TextPaint
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import ss.anoop.awesomenavigation.internal.NavigationDelegate
import ss.anoop.awesomenavigation.internal.delegates.DataDelegate
import ss.anoop.awesomenavigation.internal.delegates.ConfigDelegate
import ss.anoop.awesomenavigation.internal.delegates.ListenerDelegate
import ss.anoop.awesomenavigation.internal.delegates.ViewDelegate
import ss.anoop.awesomenavigation.internal.extensions.asRect

class PushUpNavigation(
    override val viewDelegate: ViewDelegate,
    override val dataDelegate: DataDelegate,
    override val configDelegate: ConfigDelegate,
    override val listenerDelegate: ListenerDelegate
) : NavigationDelegate {

    private val itemRects = mutableListOf<RectF>()

    private val iconRects = mutableListOf<RectF>()

    private val textRect = RectF()

    private var selectedItem = 0

    private var animator: Animator? = null

    private val textPaint by lazy {

        TextPaint(ANTI_ALIAS_FLAG).apply {
            color = configDelegate.textColor
            textSize = configDelegate.textSize
        }
    }

    init {
        viewDelegate.setTouchListener(View.OnTouchListener { _, event -> onTouch(event) })
    }

    override fun onSizeChanged() {
        dataDelegate.menuItems.forEachIndexed { index, _ ->
            itemRects.add(
                getItemRectF(
                    viewDelegate.width.div(
                        dataDelegate.menuItems.size
                    ),
                    index
                )
            )

            iconRects.add(
                getIconRectF(
                    itemRects[index]
                )
            )
        }

        val newRect = getTextRect(selectedItem)
        textRect.apply {
            left = newRect.left
            top = newRect.top
            right = newRect.right
            bottom = newRect.bottom
        }

        pushUpItem(selectedItem)
    }

    override fun draw(canvas: Canvas) {
        iconRects.forEachIndexed { index, rectF ->
            with(dataDelegate.menuItems[index]) {
                icon.bounds = rectF.asRect()
                icon.draw(canvas)
            }
        }

        canvas.drawText(
            dataDelegate.menuItems[selectedItem].title,
            textRect.left,
            textRect.bottom,
            textPaint
        )
    }

    override fun selectItem(position: Int) {
        onSelectItem(position)
    }

    private fun getItemRectF(width: Float, index: Int) = RectF(
        index.times(width),
        0f,
        index.plus(1).times(width),
        viewDelegate.height
    )

    private fun getIconRectF(itemRectF: RectF): RectF {

        val start = itemRectF.left.plus(
            itemRectF.width().minus(configDelegate.iconSize).div(2)
        )
        val top = itemRectF.top.plus(
            itemRectF.height().minus(configDelegate.iconSize).div(2)
        )

        return RectF(
            start,
            top,
            start.plus(configDelegate.iconSize),
            top.plus(configDelegate.iconSize)
        )
    }

    private fun getTextRect(selected: Int): RectF {
        val textBounds = Rect()
        textPaint.getTextBounds(
            dataDelegate.menuItems[selected].title,
            0,
            dataDelegate.menuItems[selected].title.length,
            textBounds
        )

        val itemHeight = configDelegate.iconSize.plus(
            configDelegate.iconSpacing
        ).plus(
            textBounds.height()
        )

        val start = itemRects[selected].left.plus(
            itemRects[selected].width().minus(
                textBounds.width()
            ).div(2)
        )

        val textBottom = itemRects[selected].bottom.minus(
            itemRects[selected].height().minus(
                itemHeight
            ).div(2)
        )
        return RectF(
            start,
            textBottom.minus(textBounds.height()),
            start.plus(textBounds.width()),
            textBottom
        )
    }

    private fun pushUpItem(position: Int, space: Float = configDelegate.iconSpacing) {
        iconRects[position].apply {
            bottom = textRect.top - space
            top = bottom - configDelegate.iconSize
        }
    }

    private fun pushDownItem(position: Int, space: Float = configDelegate.iconSpacing) {
        iconRects[position].apply {
            bottom = textRect.top + space
            top = bottom - configDelegate.iconSize
        }
    }


    private fun onTouch(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_UP) {
            itemRects.forEachIndexed { index, rectF ->
                if (rectF.contains(event.x, event.y)) {
                    onSelectItem(index)
                }
            }
        }
        return true
    }

    private fun onSelectItem(index: Int) {
        if (index == selectedItem) {
            listenerDelegate.navigationListener?.onReselectNavigation(
                dataDelegate.menuItems[index].id,
                index
            )
        } else {
            animateChange(selectedItem, index)
            listenerDelegate.navigationListener?.onSelectNavigation(
                dataDelegate.menuItems[index].id,
                index
            )
        }
    }

    private fun animateChange(oldIndex: Int, newIndex: Int) {
        val translation = ValueAnimator.ofFloat(textRect.left, getTextRect(newIndex).left).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = configDelegate.animationDuration
            addUpdateListener(::onTranslationUpdate)
        }.also { it.start() }

        val fadeOutIn = ValueAnimator.ofInt(255, 25, 255).apply {
            interpolator = LinearInterpolator()
            duration = configDelegate.animationDuration
            addUpdateListener(::onAlphaUpdate)
            addUpdateListener {
                if (selectedItem != newIndex &&
                    it.animatedFraction >= 0.5
                ) {
                    selectedItem = newIndex
                }
            }
        }

        val verticalTranslation = ValueAnimator.ofFloat(0f, configDelegate.iconSpacing).apply {
            interpolator = LinearInterpolator()
            duration = configDelegate.animationDuration
            addUpdateListener {
                val space = it.animatedValue as Float
                pushUpItem(newIndex, space)
                pushDownItem(oldIndex, space)
            }
        }

        animator = AnimatorSet().apply {
            playTogether(
                listOf(
                    translation,
                    fadeOutIn,
                    verticalTranslation
                )
            )
        }.also {
            it.start()
        }
    }

    private fun onTranslationUpdate(valueAnimator: ValueAnimator) {
        textRect.left = valueAnimator.animatedValue as Float
        viewDelegate.invalidate()
    }

    private fun onAlphaUpdate(valueAnimator: ValueAnimator) {
        textPaint.alpha = valueAnimator.animatedValue as Int
        viewDelegate.invalidate()
    }
}