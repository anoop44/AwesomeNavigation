package ss.anoop.awesomenavigation.internal

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatImageView

class IconDecodeHelper(private val context: Context) {

    private val imageView = AppCompatImageView(context)

    fun getIconDrawable(resId: Int): Drawable {
        imageView.setImageResource(resId)
        return imageView.drawable
    }
}