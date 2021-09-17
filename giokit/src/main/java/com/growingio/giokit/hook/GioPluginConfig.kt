package com.growingio.giokit.hook

import android.util.Log
import com.growingio.android.sdk.TrackerContext
import com.growingio.giokit.utils.CheckSelfUtils
import java.lang.StringBuilder

/**
 * <p>
 *     由插件获取的App中SDK的配置信息
 * @author cpacm 2021/8/23
 */
object GioPluginConfig {

    var xmlScheme: String? = null
    var hasGioPlugin: Boolean = false
    var dependLibs: List<String> = arrayListOf()
    var isAutoTrack = true
    var isInitLazy = true

    @JvmStatic
    fun inject(config: Map<String, Any>) {
        xmlScheme = config.get("xmlScheme") as String
        hasGioPlugin = config.get("gioPlugin") as Boolean
        val dependStr = config.get("gioDepend") as String
        dependLibs = dependStr.split("::")
    }

    //由插件注入配置信息
    fun initGioKitConfig() {

    }

    @JvmStatic
    fun checkSdkHasInit() {
        if (CheckSelfUtils.checkSdkInit()) {
            isInitLazy = false
        }
    }

    fun analyseDepend(): Triple<String, String, Boolean> {
        if (dependLibs.isEmpty()) return Triple("SDK版本", "未集成GrowingIO SDK，请按照官方文档集成", true)
        var coreLibrary: String? = null
        var autoCoreLibrary: String? = null
        for (depend in dependLibs) {
            if (depend.startsWith("com.growingio.android:")
                || depend.contains(":gio-sdk:")
                || depend.contains(":growingio")
            ) {
                if (depend.contains(":autotracker-cdp:") || depend.contains(":autotracker:")) {
                    return Triple("无埋点SDK", depend.substringAfter(":"), false)
                } else if (depend.contains(":tracker-cdp:") || depend.contains(":tracker:")) {
                    isAutoTrack = false
                    return Triple("埋点SDK", depend.substringAfter(":"), false)
                } else if (depend.contains("tracker-core:")) {
                    coreLibrary = depend.substringAfter(":")
                } else if (depend.contains("autotracker-core:")) {
                    autoCoreLibrary = depend.substringAfter(":")
                }
            }
        }
        if (autoCoreLibrary != null) return Triple("无埋点SDK核心", autoCoreLibrary, false)
        if (coreLibrary != null) return Triple("埋点SDK核心", coreLibrary, false)
        return Triple("SDK版本", "未集成GrowingIO SDK，请按照官方文档集成", true)
    }

    fun hasSdkPlugin(): Pair<String, Boolean> = when {
        isAutoTrack && hasGioPlugin -> Pair("已启用", false)
        isAutoTrack && !hasGioPlugin -> Pair("未启用,请在主项目Gradle文件中配置", true)
        !isAutoTrack && hasGioPlugin -> Pair("埋点SDK不需要插件", false)
        else -> Pair("无", false)
    }

}