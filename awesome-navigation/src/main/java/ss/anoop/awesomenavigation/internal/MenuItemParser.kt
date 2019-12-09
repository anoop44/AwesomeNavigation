package ss.anoop.awesomenavigation.internal

import android.content.Context
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import ss.anoop.awesomenavigation.internal.constant.MenuItemConstant.ATTRIBUTE_ICON
import ss.anoop.awesomenavigation.internal.constant.MenuItemConstant.ATTRIBUTE_ID
import ss.anoop.awesomenavigation.internal.constant.MenuItemConstant.ATTRIBUTE_SELECTED_ICON
import ss.anoop.awesomenavigation.internal.constant.MenuItemConstant.ATTRIBUTE_TITLE
import ss.anoop.awesomenavigation.internal.constant.MenuItemConstant.MENU_ITEM_TAG

class MenuItemParser(private val context: Context) {

    private val iconDecodeHelper = IconDecodeHelper(context)

    fun parse(menuItemRes: Int): List<MenuItem> {
        val menuList = mutableListOf<MenuItem>()
        val xmlParser = context.resources.getXml(menuItemRes)
        var tag = xmlParser.next()
        do {
            if (tag == XmlResourceParser.START_TAG &&
                xmlParser.name == MENU_ITEM_TAG
            ) {
                menuList.add(getMenuItem(xmlParser))
            }
            tag = xmlParser.next()

        } while (tag != XmlResourceParser.END_DOCUMENT)
        return menuList
    }

    private fun getMenuItem(parser: XmlResourceParser): MenuItem {
        var title = ""
        var icon: Drawable = GradientDrawable()
        var selectedIcon: Drawable? = null
        var id = 0
        for (index in 0 until parser.attributeCount) {
            when (parser.getAttributeName(index)) {
                ATTRIBUTE_ID -> id = parser.getAttributeResourceValue(index, 0)
                ATTRIBUTE_TITLE -> title = try {
                    context.getString(parser.getAttributeResourceValue(index, 0))
                } catch (exception: Exception) {
                    parser.getAttributeValue(index)
                }
                ATTRIBUTE_ICON -> icon =
                    iconDecodeHelper.getIconDrawable(parser.getAttributeResourceValue(index, 0))
                ATTRIBUTE_SELECTED_ICON -> selectedIcon =
                    iconDecodeHelper.getIconDrawable(parser.getAttributeNameResource(index))
            }
        }
        return MenuItem(
            id,
            title,
            icon,
            selectedIcon
        )
    }
}