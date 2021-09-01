package com.growingio.giokit.launch

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.core.content.res.ResourcesCompat
import com.growingio.giokit.R
import com.growingio.giokit.hover.GioHoverMenu
import com.growingio.giokit.hover.GioHoverTheme
import com.growingio.giokit.utils.BarUtils
import io.mattcarroll.hover.HoverView
import io.mattcarroll.hover.overlay.OverlayPermission

/**
 * <p>
 *     activity 生命周期管理
 * @author cpacm 2021/8/12
 */
internal class GioKitLifecycleManager(val app: Application) :
    Application.ActivityLifecycleCallbacks {

    private var hasOverlayPermission: Boolean

    init {
        app.registerActivityLifecycleCallbacks(this)
        hasOverlayPermission = checkOverlayPermission()
    }

    private fun checkOverlayPermission(): Boolean {
        return OverlayPermission.hasRuntimePermissionToDrawOverlay(app)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        if (activity is UniversalActivity) return
        attach(activity)
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        //TODO("Not yet implemented")
    }

    private fun attach(activity: Activity) {
        val decorView = activity.window.decorView as ViewGroup
        var hoverView = decorView.findViewById<HoverView>(R.id.content_hover_view)
        if (hoverView != null) return

        hoverView = HoverView.createForView(activity)
        hoverView.setMenu(
            GioHoverMenu(
                activity,
                GioHoverTheme(
                    ResourcesCompat.getColor(activity.resources, R.color.hover_primary, null),
                    ResourcesCompat.getColor(activity.resources, R.color.hover_accent, null)
                )
            )
        )
        hoverView.collapse()
        hoverView.id = R.id.content_hover_view
        val hoverParam = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        try {
            //解决由于项目集成SwipeBackLayout而出现的dokit入口不显示
            if (BarUtils.isStatusBarVisible((activity))) {
                hoverParam.topMargin = BarUtils.getStatusBarHeight(activity)
            }
            if (BarUtils.isSupportNavBar(activity)) {
                if (BarUtils.isNavBarVisible((activity))) {
                    hoverParam.bottomMargin = BarUtils.getNavBarHeight(activity)
                }
            }
        } catch (e: Exception) {
            //e.printStackTrace();
        }
        hoverView.layoutParams = hoverParam
        decorView.addView(hoverView)

    }
}