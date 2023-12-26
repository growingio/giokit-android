package com.growingio.giokit.hook

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import com.growingio.giokit.GioKit
import com.growingio.giokit.utils.CheckSdkStatusManager

/**
 * <p>
 *     由插件获取的App中SDK的配置信息
 * @author cpacm 2021/8/23
 */
object GioPluginConfig {

    var xmlScheme: String? = null
    var dependLibs: List<String> = arrayListOf()
    var isInitLazy = true

    @JvmStatic
    fun inject(context: Context, config: Map<String, Any>) {
        val attach = config.getOrElse("attach") { true } as Boolean
        if (context is Application) {
            GioKit.with(context).attach(attach).build()
        } else if (context is Activity) {
            GioKit.with(context.application).attach(attach).build()
        } else {
            Log.d("[GioKit]", "GioKit init failed, please initialized Giokit with Application or Activity")
            return
        }

        xmlScheme = config.getOrElse("xmlScheme") { "" } as String
        val dependStr = config.getOrElse("gioDepend") { "" } as String
        dependLibs = dependStr.split("##")

        checkSdkHasInit()

        Log.d("[GioKit]", "GioKit init with $config")
    }

    //由插件注入配置信息
    @JvmStatic
    fun initGioKitConfig() {

    }

    @JvmStatic
    fun checkSdkHasInit() {
        if (CheckSdkStatusManager.checkSdkInit()) {
            isInitLazy = false
        }
    }

    fun analyseDepend(): Triple<String, String, Boolean> {
        return analyseDependV3()
    }

    private fun analyseDependV3(): Triple<String, String, Boolean> {
        if (dependLibs.isEmpty()) return Triple("SDK版本", "未集成GrowingIO SDK，请按照官方文档集成", true)
        var coreLibrary: String? = null
        var autoCoreLibrary: String? = null
        var bomVersion: String = ""

        val sdkDepend =
            dependLibs.findLast { it.startsWith("com.growingio.android:") && it.contains(":autotracker-bom:") }
        sdkDepend?.apply {
            bomVersion = this.substringAfterLast(":")
        }

        for (depend in dependLibs) {
            if (depend.startsWith("com.growingio.android:")
                || depend.contains(":gio-sdk:")
                || depend.contains(":growingio")
            ) {
                var version = depend.substringAfterLast(":")
                if (version == "undefined") version = bomVersion
                if (depend.contains(":autotracker-cdp:") || depend.contains(":autotracker:")) {
                    return Triple("无埋点SDK", version, false)
                } else if (depend.contains(":tracker-cdp:") || depend.contains(":tracker:")) {
                    return Triple("埋点SDK", version, false)
                } else if (depend.contains("tracker-core:")) {
                    coreLibrary = version
                } else if (depend.contains("autotracker-core:")) {
                    autoCoreLibrary = version
                }
            }
        }
        if (autoCoreLibrary != null) return Triple("无埋点SDK核心", autoCoreLibrary, false)
        if (coreLibrary != null) return Triple("埋点SDK核心", coreLibrary, false)
        return Triple("SDK版本", "未集成GrowingIO SDK，请按照官方文档集成", true)
    }

    fun hasSdkPlugin(): Pair<String, Boolean> = Pair("已启用", false)

}