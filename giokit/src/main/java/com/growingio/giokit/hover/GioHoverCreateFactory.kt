package com.growingio.giokit.hover

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.growingio.giokit.R

/**
 * <p>
 *
 * @author cpacm 2021/9/2
 */
class GioHoverCreateFactory {

    fun createGioMenuFromCode(context: Context) :GioHoverMenu{
        val gioMenu = GioHoverMenu(
            context, GioHoverTheme(
                ResourcesCompat.getColor(context.resources, R.color.hover_primary, null),
                ResourcesCompat.getColor(context.resources, R.color.hover_accent, null)
            )
        )
        return gioMenu
    }
}