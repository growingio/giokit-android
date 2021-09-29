package com.growingio.giokit

import android.app.Activity
import android.app.Application
import android.view.View
import com.growingio.giokit.hook.GioPluginConfig
import com.growingio.giokit.hook.GioTrackInfo
import com.growingio.giokit.hover.GioKitHoverManager
import com.growingio.giokit.launch.db.DatabaseCreator
import com.growingio.giokit.launch.db.GioKitDatabase
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

    fun install(app: Application) {
        APPLICATION = app
        curActivity = WeakReference(null)
        webView = WeakReference(null)

        initGioKitConfig()

        DatabaseCreator.createDb(app) { GioKitDatabase.initDb(app) }

        gioKitHoverManager = GioKitHoverManager(app)
    }

    private fun initGioKitConfig() {
        GioPluginConfig.initGioKitConfig()

        GioTrackInfo.initGioTrack()
    }

}