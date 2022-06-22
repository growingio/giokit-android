package com.growingio.saas

import android.app.Application
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.widget.Toast
import com.growingio.android.sdk.collection.Configuration
import com.growingio.android.sdk.collection.CoreInitialize
import com.growingio.android.sdk.collection.GConfig
import com.growingio.android.sdk.collection.GrowingIO
import com.growingio.android.sdk.deeplink.DeeplinkCallback
import com.growingio.android.sdk.utils.LogUtil
import com.growingio.giokit.GioKit

/**
 * <p>
 *
 * @author cpacm 2021/9/26
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val startMill = System.currentTimeMillis()
        //Debug.startMethodTracing()
        initGioSdk()
        //Debug.stopMethodTracing()
        LogUtil.d("startup", System.currentTimeMillis() - startMill)
        GioKit.Builder(this).build()
    }

    fun initGioSdk() {
        val configuration = Configuration()
            .setProjectId("0a1b4118dd954ec3bcc69da5138bdb96")
            .setURLScheme("growing.1f85fd632636891b")
            .setMutiprocess(true)
            .setTestMode(true)
            //.setDataSourceId("12345678")
            .setDebugMode(true)
            .setOAIDEnable(true)
            .setUploadExceptionEnable(true) // DeepLink 唤醒测试连接：https://gio.ren/dweyPBZ
            // 二维码扫码地址：https://www.growingio.com/projects/QJoOzEPY/sourcemonitor/tracker
            .setDeeplinkCallback(object : DeeplinkCallback {
                override fun onReceive(params: Map<String?, String?>?, error: Int, tmClick: Long) {
                    if (params != null) {

                        Toast.makeText(
                            applicationContext,
                            "自定义参数: $params 点击时间 $tmClick",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
            .setChannel("小木2")

        GrowingIO.startWithConfiguration(this, configuration)
        GrowingIO.getInstance().track("TestSession")
    }

    fun track() {
        GrowingIO.getInstance().track("TestSession")
    }

    fun test() {
        GrowingIO.getInstance().disableDataCollect()
        GConfig.getUrlScheme()
        CoreInitialize.config()
        GrowingIO.getInstance().track("test")
    }
}