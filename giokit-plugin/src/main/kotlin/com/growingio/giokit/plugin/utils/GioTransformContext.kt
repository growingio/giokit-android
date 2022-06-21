package com.growingio.giokit.plugin.utils

import com.growingio.giokit.plugin.extension.GioKitExtension
import java.io.File
import java.io.Serializable
import java.lang.StringBuilder

interface GioTransformContext {
    val className: String

    fun isAssignable(subClazz: String, superClazz: String): Boolean

    fun classIncluded(clazz: String): Boolean

    val gioConfig: GioConfig

    val generatedDir: File
}

/**
 * 相关配置和变量保存区
 */
data class GioConfig(val type: String) : Serializable {
    var domain: String = ""
    var xmlScheme: String = ""
    var gioSdks = mutableSetOf<DependLib>()
    var hasGioPlugin = false
    var gioTracks = mutableSetOf<GioTrackHook>()
    var buildDir: File? = null
    var trackFinder = TrackFinder()

    fun getVisitorCodeFile(): File {
        return File(buildDir!!.absolutePath+ "/tmp/","giokit_track_scan.txt")
    }

    fun setPluginExtension(ext: GioKitExtension) {
        this.trackFinder.enable = ext.trackFinder.enable
        this.trackFinder.domain = ext.trackFinder.domain
        this.trackFinder.className = ext.trackFinder.className
        this.trackFinder.methodName = ext.trackFinder.methodName
        DEBUG_ENABLE = ext.debugMode
    }

    fun getGioDepend(sdks: Set<DependLib>): String {
        val sb = StringBuilder()
        sdks.forEach { dependLib ->
            sb.append(dependLib.variant).append(":").append(dependLib.version)
            if (sdks.last() != dependLib) {
                sb.append("##")
            }
        }
        return sb.toString()
    }
}

class TrackFinder : Serializable {
    var enable: Boolean = false
    var domain: MutableList<String> = mutableListOf<String>()
    var className: String = ""
    var methodName: String = ""
}

data class GioTrackHook(
    val className: String,
    val methodName: String,
    var selfClassName: String = "",
    var selfMethodName: String = ""
) : Serializable {
}

data class DependLib(val variant: String, val version: String) : Serializable

