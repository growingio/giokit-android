package com.growingio.giokit

import android.app.Activity
import android.app.Application
import android.view.View
import com.growingio.giokit.apm.GMonitorManager
import com.growingio.giokit.hook.GioPluginConfig
import com.growingio.giokit.hook.GioTrackInfo
import com.growingio.giokit.hover.GioKitHoverManager
import com.growingio.giokit.launch.db.GioKitDatabase
import com.growingio.giokit.utils.CheckSdkStatusManager
import java.lang.ref.WeakReference

/**
 * <p>
 *
 * @author cpacm 2021/8/12
 */
internal object GioKitImpl {

    lateinit var APPLICATION: Application
    lateinit var gioKitHoverManager: GioKitHoverManager
    lateinit var curActivity: WeakReference<Activity>
    lateinit var webView: WeakReference<View>

    var inited = false
    var launchTime: Long = System.currentTimeMillis()

    fun install(app: Application) {
        APPLICATION = app
        launchTime = System.currentTimeMillis()
        curActivity = WeakReference(null)
        webView = WeakReference(null)

        initGioKitConfig()

        GioKitDatabase.initDb(app)

        gioKitHoverManager = GioKitHoverManager(app)

        initWithGrowingIOSDK()

        GMonitorManager.getInstance(app)

        inited = true
    }

    private fun initGioKitConfig() {
        GioPluginConfig.initGioKitConfig()

        GioTrackInfo.initGioTrack()
    }

    private fun initWithGrowingIOSDK() {
        // deal v3sdk with EventFilter
        CheckSdkStatusManager.getInstance().eventFilterProxy()

        // monitor insert events

    }

}