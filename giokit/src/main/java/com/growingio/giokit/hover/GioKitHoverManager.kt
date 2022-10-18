package com.growingio.giokit.hover

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.growingio.giokit.GioKitImpl
import com.growingio.giokit.R
import com.growingio.giokit.circle.CircleAnchorView
import com.growingio.giokit.instant.InstantEventCache
import com.growingio.giokit.instant.InstantEventView
import com.growingio.giokit.launch.UniversalActivity
import com.growingio.giokit.setting.GiokitSettingActivity
import com.growingio.giokit.utils.BarUtils
import io.mattcarroll.hover.HoverView
import io.mattcarroll.hover.overlay.OverlayPermission
import java.lang.ref.WeakReference

/**
 * <p>
 *     activity 生命周期管理
 * @author cpacm 2021/8/12
 */
class GioKitHoverManager(val app: Application) :
    Application.ActivityLifecycleCallbacks {

    var hasOverlayPermission: Boolean
    private var startedActivityCounts: Int = 0
    var hoverView: HoverView? = null
    var anchorView: CircleAnchorView? = null
    var instantEventView: InstantEventView? = null

    init {
        app.registerActivityLifecycleCallbacks(this)
        hasOverlayPermission = checkOverlayPermission()
    }

    fun startCircle(context: Context) {
        anchorView = CircleAnchorView(context)
        anchorView?.show()
    }

    fun removeFloatingView() {
        removeCircle()
        instantEventView?.hide()
    }

    fun restoreFloatingView() {
        instantEventView?.show()
    }

    private fun removeCircle() {
        anchorView?.remove()
        anchorView = null
    }

    fun startInstantMonitor(context: Context) {
        InstantEventCache.enableInstantEventMonitor()
        if (instantEventView != null && instantEventView?.isShow() == true) return
        instantEventView = InstantEventView(context)
        instantEventView?.show()
    }

    fun removeInstantMonitor() {
        InstantEventCache.disableInstantEventMonitor()
        instantEventView?.remove()
        instantEventView = null
    }

    private fun checkOverlayPermission(): Boolean {
        return OverlayPermission.hasRuntimePermissionToDrawOverlay(app)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        try {
            if (startedActivityCounts == 0) {
                notifyForeground(activity)
            }
            startedActivityCounts++
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResumed(activity: Activity) {
        GioKitImpl.curActivity = WeakReference(activity)
        if (activity is UniversalActivity || activity is GiokitSettingActivity) return
        if (hasOverlayPermission) {
            detach(activity)
            return
        }
        attach(activity)
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {
        try {
            startedActivityCounts--
            if (startedActivityCounts == 0) {
                notifyBackground()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    fun notifyOverlay(context: Context) {
        if (context is Activity) {
            //将 hover 切换至悬浮窗
            hasOverlayPermission = true
            onActivityResumed(context)

            notifyForeground(context)
        }
    }

    /**
     * 应用切换到前台
     */
    private fun notifyForeground(activity: Activity) {
        if (hasOverlayPermission) {
            GioHoverMenuService.showFloatingMenu(activity)
            restoreFloatingView()
        }
    }

    fun setupHoverView(hoverView: HoverView) {
        this.hoverView = hoverView
        this.hoverView?.addOnExpandAndCollapseListener(HoverViewCollapseAndExpandListener())
    }

    /**
     * 应用切换到后台
     */
    private fun notifyBackground() {
        if (hasOverlayPermission) {
            hoverView?.collapse()
            hoverView?.removeFromWindow()
        } else {
            hoverView?.collapse()
        }
        removeFloatingView()
    }

    private fun detach(activity: Activity) {
        val decorView = activity.window.decorView as ViewGroup
        val hoverView = decorView.findViewById<HoverView>(R.id.content_hover_view) ?: return
        decorView.removeView(hoverView)
    }

    private fun attach(activity: Activity) {
        val decorView = activity.window.decorView as ViewGroup
        var hoverView = decorView.findViewById<HoverView>(R.id.content_hover_view)
        if (hoverView != null) return

        hoverView = HoverView.createForView(activity)
        hoverView.setMenu(GioHoverCreateFactory().createGioMenuFromCode(activity))
        hoverView.collapse()
        hoverView.id = R.id.content_hover_view
        val hoverParam = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        try {
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
        hoverView.addOnExpandAndCollapseListener(HoverViewCollapseAndExpandListener())
        hoverView.layoutParams = hoverParam
        decorView.addView(hoverView)
    }

    inner class HoverViewCollapseAndExpandListener : HoverView.Listener {
        override fun onExpanding() {
            removeFloatingView()
        }

        override fun onExpanded() {}

        override fun onCollapsing() {}

        override fun onCollapsed() {
            restoreFloatingView()
        }

        override fun onClosing() {}

        override fun onClosed() {
            removeFloatingView()
        }

    }

}