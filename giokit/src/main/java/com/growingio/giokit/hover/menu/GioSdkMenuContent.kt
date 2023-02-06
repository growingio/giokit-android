package com.growingio.giokit.hover.menu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.growingio.giokit.GioKitImpl
import com.growingio.giokit.R
import com.growingio.giokit.hook.GioPluginConfig
import com.growingio.giokit.instant.InstantEventCache
import com.growingio.giokit.launch.LaunchPage
import com.growingio.giokit.launch.UniversalActivity
import com.growingio.giokit.setting.GiokitSettingActivity
import io.mattcarroll.hover.Content
import io.mattcarroll.hover.overlay.OverlayPermission

/**
 * <p>
 *
 * @author cpacm 2021/8/26
 */
class GioSdkMenuContent(context: Context) : FrameLayout(context), Content, View.OnClickListener {


    init {
        LayoutInflater.from(context).inflate(R.layout.giokit_view_content_menu, this, true)

        val sdkInfoPage = findViewById(R.id.sdkInfoLayout) as View
        sdkInfoPage.setOnClickListener(this)

        val sdkCodeLayout = findViewById(R.id.sdkCodeLayout) as View
        sdkCodeLayout.setOnClickListener(this)

        val sdkDataLayout = findViewById(R.id.sdkDataLayout) as View
        sdkDataLayout.setOnClickListener(this)

        val sdkTrackLayout = findViewById(R.id.sdkTrackLayout) as View
        sdkTrackLayout.setOnClickListener(this)

        val sdkHttpLayout = findViewById(R.id.sdkHttpLayout) as View
        sdkHttpLayout.setOnClickListener(this)

        val sdkInstantLayout = findViewById(R.id.sdkInstantLayout) as View
        sdkInstantLayout.setOnClickListener(this)

        val sdkH5DoorLayout = findViewById<View>(R.id.sdkH5DoorLayout)
        sdkH5DoorLayout.setOnClickListener(this)
        if (GioPluginConfig.isSaasSdk) {
            sdkH5DoorLayout.visibility = View.GONE
        }

        val sdkCrashLayout = findViewById(R.id.sdkCrashLayout) as View
        sdkCrashLayout.setOnClickListener(this)

        val sdkPrefLayout = findViewById(R.id.sdkPrefLayout) as View
        sdkPrefLayout.setOnClickListener(this)

        val sdkCommonSettingLayout = findViewById(R.id.sdkCommonSettingLayout) as View
        sdkCommonSettingLayout.setOnClickListener(this)

    }

    override fun getView(): View {
        return this
    }

    override fun isFullscreen(): Boolean {
        return true
    }

    override fun onShown() {
        if (InstantEventCache.isInstantEventMonitorEnable()) {
            val instantTitle = findViewById<TextView>(R.id.instantTitle)
            instantTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
            instantTitle.setText(R.string.giokit_menu_monitor_running)
            instantTitle.requestFocus()

            val instantLogo = findViewById<ImageView>(R.id.instantLogo)
            instantLogo.setBackgroundResource(R.drawable.giokit_check_menu_button_close_bg)
        } else {
            val instantTitle = findViewById<TextView>(R.id.instantTitle)
            instantTitle.ellipsize = null
            instantTitle.setText(R.string.giokit_menu_monitor)

            val instantLogo = findViewById<ImageView>(R.id.instantLogo)
            instantLogo.setBackgroundResource(R.drawable.giokit_check_menu_button_bg)
        }
    }

    override fun onHidden() {

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sdkInfoLayout -> {
                GioKitImpl.gioKitHoverManager.hoverView?.collapse()
                context.startActivity(Intent(context, UniversalActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(LaunchPage.LAUNCH_FRAGMENT_INDEX, LaunchPage.SDKINFO_PAGE)
                })
            }
            R.id.sdkCodeLayout -> {
                GioKitImpl.gioKitHoverManager.hoverView?.collapse()
                context.startActivity(Intent(context, UniversalActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(LaunchPage.LAUNCH_FRAGMENT_INDEX, LaunchPage.SDKCODE_PAGE)
                })
            }
            R.id.sdkDataLayout -> {
                GioKitImpl.gioKitHoverManager.hoverView?.collapse()
                context.startActivity(Intent(context, UniversalActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(LaunchPage.LAUNCH_FRAGMENT_INDEX, LaunchPage.SDKDATA_PAGE)
                })
            }
            R.id.sdkTrackLayout -> {
                if (OverlayPermission.hasRuntimePermissionToDrawOverlay(context)) {
                    GioKitImpl.gioKitHoverManager.hoverView?.collapse()
                    GioKitImpl.gioKitHoverManager.notifyOverlay(context)

                    GioKitImpl.gioKitHoverManager.startCircle(context)

                } else {
                    val intent = OverlayPermission.createIntentToRequestOverlayPermission(context)
                    if (context !is Activity) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
            }
            R.id.sdkInstantLayout -> {
                if (InstantEventCache.isInstantEventMonitorEnable()) {
                    InstantEventCache.disableInstantEventMonitor()
                    GioKitImpl.gioKitHoverManager.removeInstantMonitor()
                    GioKitImpl.gioKitHoverManager.hoverView?.collapse()
                    return
                }
                if (OverlayPermission.hasRuntimePermissionToDrawOverlay(context)) {
                    GioKitImpl.gioKitHoverManager.hoverView?.collapse()
                    GioKitImpl.gioKitHoverManager.notifyOverlay(context)

                    GioKitImpl.gioKitHoverManager.startInstantMonitor(context)

                } else {
                    val intent = OverlayPermission.createIntentToRequestOverlayPermission(context)
                    if (context !is Activity) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
            }
            R.id.sdkHttpLayout -> {
                GioKitImpl.gioKitHoverManager.hoverView?.collapse()
                context.startActivity(Intent(context, UniversalActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(LaunchPage.LAUNCH_FRAGMENT_INDEX, LaunchPage.SDKHTTP_PAGE)
                })
            }
            R.id.sdkH5DoorLayout -> {
                GioKitImpl.gioKitHoverManager.hoverView?.collapse()
                context.startActivity(Intent(context, UniversalActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(LaunchPage.LAUNCH_FRAGMENT_INDEX, LaunchPage.SDKH5DOOR_PAGE)
                })
            }

            R.id.sdkCrashLayout -> {
                GioKitImpl.gioKitHoverManager.hoverView?.collapse()
                context.startActivity(Intent(context, UniversalActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(LaunchPage.LAUNCH_FRAGMENT_INDEX, LaunchPage.SDKERROR_PAGE)
                })
            }
            R.id.sdkPrefLayout -> {
                GioKitImpl.gioKitHoverManager.hoverView?.collapse()
                context.startActivity(Intent(context, UniversalActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(LaunchPage.LAUNCH_FRAGMENT_INDEX, LaunchPage.SDKPREF_PAGE)
                })
            }

            // 通用设置
            R.id.sdkCommonSettingLayout -> {
                GioKitImpl.gioKitHoverManager.hoverView?.collapse()
                context.startActivity(Intent(context, GiokitSettingActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(LaunchPage.LAUNCH_FRAGMENT_INDEX, LaunchPage.SDK_COMMONSETTING_PAGE)
                    putExtra(LaunchPage.LAUNCH_FRAGMENT_TITLE, R.string.giokit_menu_common_setting)
                })
            }
        }
    }
}