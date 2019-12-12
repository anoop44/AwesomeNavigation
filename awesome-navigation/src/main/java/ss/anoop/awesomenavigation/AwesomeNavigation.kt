package ss.anoop.awesomenavigation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.*
import ss.anoop.awesomenavigation.internal.*
import ss.anoop.awesomenavigation.internal.NavigationDelegate
import ss.anoop.awesomenavigation.internal.delegates.ConfigDelegate
import ss.anoop.awesomenavigation.internal.delegates.DataDelegate
import ss.anoop.awesomenavigation.internal.delegates.ListenerDelegate
import ss.anoop.awesomenavigation.internal.delegates.ViewDelegate
import ss.anoop.awesomenavigation.internal.navigations.PushUpNavigation
import ss.anoop.awesomenavigation.internal.utils.dpToPx
import ss.anoop.awesomenavigation.internal.utils.spToPx

class AwesomeNavigation
@JvmOverloads
constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    private val defResStyle: Int = 0
) : View(
    context,
    attributeSet,
    defResStyle
),
    ViewDelegate,
    DataDelegate,
    ConfigDelegate,
    ListenerDelegate {

    private var navigationItems = emptyList<MenuItem>()

    private var navigationDelegate: NavigationDelegate = PushUpNavigation(
        viewDelegate = this,
        dataDelegate = this,
        configDelegate = this,
        listenerDelegate = this
    )

    override val width: Float
        get() = getWidth().toFloat()

    override val height: Float
        get() = getHeight().toFloat()

    override val menuItems: List<MenuItem>
        get() = navigationItems

    override val iconSize: Float
        get() = _iconSize

    override val iconSpacing: Float
        get() = _iconSpacing

    override val textSize: Float
        get() = _textSize

    override val textColor: Int
        get() = _textColor

    override val animationDuration: Long
        get() = _animationDuration

    override val navigationListener: OnNavigationSelectedListener?
        get() = _navigationListener

    private var _iconSize = dpToPx(24f, resources)

    private var _iconSpacing = dpToPx(4f, resources)

    private var _textSize = spToPx(12f, resources)

    private var _textColor = Color.BLACK

    private var _animationDuration = 500L

    private var _navigationListener: OnNavigationSelectedListener? = null

    init {
        attributeSet?.let(::initAttrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        require(
            getMode(widthMeasureSpec) != UNSPECIFIED &&
                    getMode(heightMeasureSpec) != UNSPECIFIED
        ) { "Width and Height must be exact" }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        navigationDelegate.onSizeChanged()
    }

    override fun onDraw(canvas: Canvas) {
        navigationDelegate.draw(canvas)
    }

    override fun setTouchListener(onTouchListener: OnTouchListener) {
        setOnTouchListener(onTouchListener)
    }

    fun setOnNavigationSelectedListener(navigationSelectedListener: OnNavigationSelectedListener) {
        _navigationListener = navigationSelectedListener
    }

    fun selectItem(position: Int) {
        require(
            position > -1 && position < navigationItems.size
        ) {
            "Invalid position"
        }

        navigationDelegate.selectItem(position)
    }

    private fun initAttrs(attributeSet: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.AwesomeNavigation,
            0,
            defResStyle
        )
        navigationItems = MenuItemParser(context)
            .parse(
                typedArray.getResourceId(R.styleable.AwesomeNavigation_navItems, NO_ID)
            )
        _animationDuration =
            typedArray.getInteger(R.styleable.AwesomeNavigation_itemChangeDuration, 500).toLong()
        _iconSize = typedArray.getDimension(R.styleable.AwesomeNavigation_iconSize, _iconSize)
        _iconSpacing = typedArray.getDimension(R.styleable.AwesomeNavigation_spacing, _iconSpacing)
        _textSize = typedArray.getDimension(R.styleable.AwesomeNavigation_textSize, _textSize)
        _textColor = typedArray.getColor(R.styleable.AwesomeNavigation_textColor, _textColor)
        typedArray.recycle()
    }
}