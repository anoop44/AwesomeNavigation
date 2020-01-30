package ss.anoop.awesomenavigation.internal.navigations

import android.animation.*
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.text.TextPaint
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import ss.anoop.awesomenavigation.internal.NavigationDelegate
import ss.anoop.awesomenavigation.internal.delegates.ConfigDelegate
import ss.anoop.awesomenavigation.internal.delegates.DataDelegate
import ss.anoop.awesomenavigation.internal.delegates.ListenerDelegate
import ss.anoop.awesomenavigation.internal.delegates.ViewDelegate
import ss.anoop.awesomenavigation.internal.extensions.asRect
import ss.anoop.awesomenavigation.internal.utils.DefaultAnimatorListener
import kotlin.math.max

class NavigationStyle2(
    override val dataDelegate: DataDelegate,
    override val viewDelegate: ViewDelegate,
    override val configDelegate: ConfigDelegate,
    override val listenerDelegate: ListenerDelegate
) : NavigationDelegate, View.OnTouchListener {

    private val textPaint = TextPaint(ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
    }

    private val selectionPaint = Paint(ANTI_ALIAS_FLAG)

    private val iconRects = mutableListOf<RectF>()

    private val itemRects = mutableListOf<RectF>()

    private val textPoints = listOf(
        PointF(),
        PointF(),
        PointF(),
        PointF()
    )

    private val selectionRect = RectF()

    private var iconTranslationDistance = 0f

    private var textHeight = 0f

    private var selectedItemIndex = -1

    init {
        viewDelegate.setTouchListener(this)
    }

    override fun selectItem(position: Int) {
        onSelectItem(position)
    }

    override fun onSizeChanged() {
        textPaint.apply {
            color = configDelegate.textColor
            textSize = configDelegate.textSize
        }

        var longestTextSize = 0f
        dataDelegate.menuItems.forEach {
            longestTextSize = max(
                longestTextSize,
                textPaint.measureText(
                    it.title
                )
            )
        }

        val totalSpace = longestTextSize.times(
            dataDelegate.menuItems.size
        ).plus(
            configDelegate.iconSize.times(
                dataDelegate.menuItems.size
            )
        ).plus(
            configDelegate.iconSpacing.times(
                dataDelegate.menuItems.size.times(3)
            )
        ).plus(
            configDelegate.iconSpacing.times(
                dataDelegate.menuItems.size.times(2)
            )
        )

        val offset = viewDelegate.width
            .minus(totalSpace)
            .div(2f)

        val singleItemWidth = totalSpace.div(
            dataDelegate.menuItems.size
        )
        val top = viewDelegate.height.div(2f).minus(
            configDelegate.iconSize.div(2).plus(
                configDelegate.iconSpacing
            )
        )

        dataDelegate.menuItems.forEachIndexed { index, _ ->
            var left = index.times(singleItemWidth)
                .plus(offset)

            itemRects.add(
                RectF(
                    left,
                    top,
                    left.plus(
                        singleItemWidth
                    ),
                    top.plus(
                        configDelegate.iconSize.plus(
                            configDelegate.iconSpacing.times(2)
                        )
                    )
                )
            )
            left += singleItemWidth
                .div(2)
                .minus(
                    configDelegate
                        .iconSize
                        .div(2)
                )

            iconRects.add(
                RectF(
                    left,
                    top.plus(
                        configDelegate.iconSpacing
                    ),
                    left.plus(
                        configDelegate.iconSize
                    ),
                    top.plus(
                        configDelegate.iconSize.plus(
                            configDelegate.iconSpacing
                        )
                    )
                )
            )
        }

        val firstItemRect = itemRects.firstOrNull()
        val firstIconRect = iconRects.firstOrNull()
        if (firstItemRect != null &&
            firstIconRect != null
        ) {
            iconTranslationDistance = firstIconRect.left.minus(
                firstItemRect.left.plus(
                    configDelegate.iconSpacing.times(2)
                )
            )
            textPaint.apply {
                textSize = configDelegate.textSize
                color = configDelegate.textColor
            }
            val textBounds = Rect()
            textPaint.getTextBounds(
                dataDelegate.menuItems[0].title,
                0,
                dataDelegate.menuItems[0].title.length,
                textBounds
            )
            textHeight = textBounds.height().toFloat()

            selectionPaint.color = configDelegate.selectionColor
            selectionRect.set(firstItemRect)
            onSelectItem(0)
        }

    }

    override fun draw(canvas: Canvas) {

        canvas.drawRoundRect(
            selectionRect,
            configDelegate.cornerRadius,
            configDelegate.cornerRadius,
            selectionPaint
        )

        dataDelegate.menuItems.forEachIndexed { index, menuItem ->
            with(menuItem.icon) {
                bounds = iconRects[index].asRect()
                draw(canvas)
            }
        }

        textPoints.forEachIndexed { index, point ->
            if (point.y > 0) {
                canvas.drawText(
                    dataDelegate.menuItems[index].title,
                    point.x,
                    point.y,
                    textPaint
                )
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.actionMasked == MotionEvent.ACTION_UP) {
            itemRects.firstOrNull { it.contains(event.x, event.y) }?.let {
                val index = itemRects.indexOf(it)
                onSelectItem(index)
                if(selectedItemIndex == index){
                    listenerDelegate.navigationListener?.onReselectNavigation(
                        dataDelegate.menuItems[index].id,
                        index
                    )
                } else {
                    listenerDelegate.navigationListener?.onSelectNavigation(
                        dataDelegate.menuItems[index].id,
                        index
                    )
                }
            }
        }

        return true
    }

    private fun onSelectItem(index: Int) {
        if (selectedItemIndex >= 0) {
            collapseItem(selectedItemIndex)
            moveSelection(index)
        }
        expandItem(index)
    }

    private fun expandItem(index: Int) {

        textPoints[index].apply {
            x = iconRects[index].left.plus(
                configDelegate.iconSpacing
            )
        }

        val textTranslation = textTranslation(
            index,
            viewDelegate.height.minus(textHeight),
            viewDelegate.height.div(2f).plus(
                textHeight.div(2f)
            )
        )

        val iconTranslation = iconTranslation(
            index,
            iconRects[index].left,
            iconRects[index].left - iconTranslationDistance
        )

        AnimatorSet().apply {
            playTogether(
                iconTranslation,
                textTranslation
            )
        }.also {
            it.start()
        }

        selectedItemIndex = index
    }

    private fun collapseItem(index: Int) {

        val textTranslation = textTranslation(
            index,
            viewDelegate.height.div(2f).plus(
                textHeight.div(2f)
            ),
            viewDelegate.height.minus(textHeight)
        )

        val iconTranslation = iconTranslation(
            index,
            iconRects[index].left,
            iconRects[index].left + iconTranslationDistance
        )

        AnimatorSet().apply {
            playTogether(
                iconTranslation,
                textTranslation
            )
            addListener(
                object : DefaultAnimatorListener() {
                    override fun onAnimationEnd(animation: Animator?) {
                        textPoints[index].y = 0f
                    }
                }
            )
        }.also {
            it.start()
        }
    }

    private fun moveSelection(index: Int) {
        ValueAnimator.ofFloat(
            itemRects[selectedItemIndex].left,
            itemRects[index].left
        ).apply {
            duration = configDelegate.animationDuration
            interpolator = OvershootInterpolator()
            addUpdateListener {
                val animatedValue = it.animatedValue as Float
                selectionRect.apply {
                    right = animatedValue.plus(width())
                    left = animatedValue
                }
                viewDelegate.invalidate()
            }
        }.also {
            it.start()
        }
    }

    private fun textTranslation(
        index: Int,
        fromY: Float,
        toY: Float
    ): Animator {
        return ValueAnimator.ofFloat(
            fromY,
            toY
        ).apply {
            duration = configDelegate.animationDuration.div(2)
            interpolator = LinearInterpolator()
            addUpdateListener {
                val animatedValue = it.animatedValue as Float
                textPoints[index].apply {
                    y = animatedValue
                }

                viewDelegate.invalidate()
            }
        }
    }

    private fun iconTranslation(
        index: Int,
        fromX: Float,
        toX: Float
    ): Animator {
        return ValueAnimator.ofFloat(
            fromX,
            toX
        ).apply {
            duration = configDelegate.animationDuration.div(2)
            interpolator = LinearInterpolator()
            addUpdateListener {
                val animatedValue = it.animatedValue as Float
                iconRects[index].apply {
                    left = animatedValue
                    right = animatedValue.plus(
                        configDelegate.iconSize
                    )
                }

                viewDelegate.invalidate()
            }
        }
    }
}