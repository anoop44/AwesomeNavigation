package ss.anoop.awesomenavigation.internal.utils

import android.content.res.Resources
import android.util.TypedValue


internal fun dpToPx(dp: Float, resources: Resources) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

internal fun spToPx(sp: Float, resources: Resources) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)