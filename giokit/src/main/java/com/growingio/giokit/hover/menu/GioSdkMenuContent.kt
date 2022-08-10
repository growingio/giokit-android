package com.growingio.giokit.hover.menu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.growingio.giokit.GioKitImpl
import com.growingio.giokit.R
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
            R.id.sdkHttpLayout ->{
                GioKitImpl.gioKitHoverManager.hoverView?.collapse()
                context.startActivity(Intent(context, UniversalActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(LaunchPage.LAUNCH_FRAGMENT_INDEX, LaunchPage.SDKHTTP_PAGE)
                })
            }

            // 通用设置
            R.id.sdkCommonSettingLayout ->{
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