package com.growingio.giokit.plugin.utils

import com.didiglobal.booster.transform.TransformContext
import com.growingio.giokit.plugin.extension.GioKitExtension
import java.lang.StringBuilder

interface GioTransformContext : TransformContext {
    val gioConfig: GioConfig
}

interface GioTransformListener {
    fun onPreTransform(context: GioTransformContext) {}
    fun onPostTransform(context: GioTransformContext) {}
}


/**
 * 相关配置和变量保存区
 */
data class GioConfig(val type: String) {
    var domain: String = ""
    var xmlScheme: String = ""
    var gioSdks = mutableSetOf<DependLib>()
    var hasGioPlugin = false
    var gioTracks = mutableSetOf<GioTrackHook>()
    var gioKitExt = GioKitExtension()

    fun setPluginExtension(ext: GioKitExtension) {
        this.gioKitExt = ext
        GIOKIT_LOG_ENABLE = ext.debugMode
    }

    fun getGioDepend(sdks: Set<DependLib>): String {
        val sb = StringBuilder()
        sdks.forEach { dependLib ->
            sb.append(dependLib.variant)
            if (sdks.last() != dependLib) {
                sb.append("::")
            }
        }
        return sb.toString()
    }
}

data class GioTrackHook(
    val className: String,
    val methodName: String,
    var selfClassName: String = "",
    var selfMethodName: String = ""
)

data class DependLib(val variant: String, val fileSize: Long)

