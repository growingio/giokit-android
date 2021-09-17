package com.growingio.giokit.demo

import android.app.Application
import com.growingio.android.sdk.TrackerContext
import com.growingio.android.sdk.autotrack.CdpAutotrackConfiguration
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.android.sdk.autotrack.IgnorePolicy
import com.growingio.android.sdk.track.events.helper.EventExcludeFilter
import com.growingio.android.sdk.track.events.helper.FieldIgnoreFilter
import com.growingio.giokit.GioKit

/**
 * <p>
 *
 * @author cpacm 2021/8/12
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initGioSdk()

        GioKit.Builder(this).build()
    }

    fun onTest() {
        GrowingAutotracker.get().trackCustomEvent("test")
        TrackerContext.initializedSuccessfully()
    }

    fun initGioSdk() {
        val config = CdpAutotrackConfiguration("xxxxxxxx", "growing.xxxxxxxx")
            .setDataSourceId("xxxxxxxx")
            .setDebugEnabled(true)
            .setDataCollectionServerHost("http://localhost:8080")
            .setExcludeEvent(EventExcludeFilter.FORM_SUBMIT)
            .setExcludeEvent(EventExcludeFilter.VIEW_CHANGE)
            .setIgnoreField(FieldIgnoreFilter.NETWORK_STATE)
            .setChannel("test")


        GrowingAutotracker.startWithConfiguration(this, config)
    }
}