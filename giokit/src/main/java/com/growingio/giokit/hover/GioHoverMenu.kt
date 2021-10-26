package com.growingio.giokit.hover

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.growingio.giokit.R
import com.growingio.giokit.hover.check.CheckSelfContent
import com.growingio.giokit.hover.menu.GioSdkMenuContent
import io.mattcarroll.hover.Content
import io.mattcarroll.hover.HoverMenu
import java.lang.RuntimeException

/**
 * <p>
 *
 * @author cpacm 2021/8/12
 */
class GioHoverMenu(val context: Context, val theme: GioHoverTheme) : HoverMenu() {

    private val sections = arrayListOf<Section>()

    init {
        sections.add(
            Section(
                SectionId(SELF_CHECK),
                createTab(SELF_CHECK),
                createContent(SELF_CHECK)
            )
        )
        sections.add(
            Section(
                SectionId(GIO_MENU),
                createTab(GIO_MENU),
                createContent(GIO_MENU)
            )
        )
    }

    override fun getId(): String {
        return "gio-menu"
    }

    override fun getSectionCount(): Int {
        return sections.size
    }

    override fun getSection(index: Int): Section {
        return sections.get(index)
    }

    override fun getSection(sectionId: SectionId): Section? {
        for (section in sections) {
            if (section.id == sectionId) {
                return section
            }
        }
        return null
    }

    override fun getSections(): MutableList<Section> {
        return ArrayList(sections)
    }

    private fun createContent(sectionId: String): Content {
        when (sectionId) {
            SELF_CHECK -> {
                return CheckSelfContent(context)
            }
            GIO_MENU ->{
                return GioSdkMenuContent(context)
            }
            else -> {
                throw RuntimeException("Unknown tab selected: " + sectionId)
            }
        }
    }

    private fun createTab(sectionId: String): View {
        when (sectionId) {
            SELF_CHECK -> {
                return createTab(R.drawable.ic_giokit_g, theme.accentColor, null);
            }
            GIO_MENU->{
                return createTab(R.drawable.ic_giokit_menu, theme.accentColor, null);
            }
            else -> {
                throw RuntimeException("Unknown tab selected: " + sectionId)
            }
        }
    }

    private fun createTab(
        @DrawableRes tabBitmapRes: Int,
        @ColorInt backgroundColor: Int,
        @ColorInt iconColor: Int?
    ): View {
        val resource = context.resources

        val tabView = GioTabView(
            context,
            ResourcesCompat.getDrawable(resource, R.drawable.giokit_tab_background, null),
            ResourcesCompat.getDrawable(resource, tabBitmapRes, null)
        )
        tabView.setTabBackgroundColor(backgroundColor)
        tabView.setTabForegroundColor(iconColor)
        return tabView
    }

    companion object {
        // 默认Tab分页
        const val SELF_CHECK = "self-check"
        const val GIO_MENU = "gio-menu"
    }
}