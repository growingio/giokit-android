package com.growingio.giokit.v3

import android.app.Application
import com.growingio.android.encoder.EncoderLibraryGioModule
import com.growingio.android.hybrid.HybridLibraryGioModule
import com.growingio.android.sdk.TrackerContext
import com.growingio.android.sdk.autotrack.AutotrackConfiguration
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.giokit.hook.GioPluginConfig

/**
 * <p>
 *
 * @author cpacm 2021/8/12
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initGioSdk()

        //GioKit.with(this).attach(true).build()

        val map = hashMapOf<String, Any>()
        map["attach"] = true
        map["xmlScheme"] = "growingio.1234567678"
        GioPluginConfig.inject(this, map)
    }

    fun onTest() {
        GrowingAutotracker.get().trackCustomEvent("test")
        TrackerContext.initializedSuccessfully()
    }

    fun initGioSdk() {
        val config = AutotrackConfiguration("91eaf9b283361032", "growing.8226cee4b794ebd0")
            .setDataSourceId("951f87ed30c9d9a3")
            .setDebugEnabled(true)
            .setDataCollectionServerHost("http://117.50.105.254:8080")
            .addPreloadComponent(EncoderLibraryGioModule())
            .addPreloadComponent(HybridLibraryGioModule())
            .setChannel("test")

        GrowingAutotracker.startWithConfiguration(this, config)

    }
}