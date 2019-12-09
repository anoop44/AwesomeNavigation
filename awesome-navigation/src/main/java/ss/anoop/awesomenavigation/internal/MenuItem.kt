package ss.anoop.awesomenavigation.internal

import android.graphics.drawable.Drawable

data class MenuItem(
    val id: Int,
    val title: String,
    val icon: Drawable,
    val selectedIcon: Drawable?
)