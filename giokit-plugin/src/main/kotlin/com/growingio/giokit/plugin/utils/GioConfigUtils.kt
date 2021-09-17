package com.growingio.giokit.plugin.utils

import com.growingio.giokit.plugin.extension.GioKitExtension
import java.lang.StringBuilder

/**
 * <p>
 *     相关配置和变量保存区
 * @author cpacm 2021/8/17
 */
object GioConfigUtils {
    var GIOKIT_LOG_ENABLE = false

    fun isLogEnable(): Boolean {
        return GIOKIT_LOG_ENABLE || this.gioKitExt.debugMode
    }

    var defaultDomain: String? = null
    val dependLibs = mutableSetOf<DependLib>()
    var xmlScheme: String? = null
    var gioSdks = mutableSetOf<DependLib>()
    var hasGioPlugin = false
    var gioTracks = mutableSetOf<GioTrackHook>()

    var gioKitExt = GioKitExtension()
        private set

    fun setPluginExtension(ext: GioKitExtension) {
        this.gioKitExt = ext
    }

    fun getGioDepend(): String {
        val sb = StringBuilder()
        gioSdks.forEach { dependLib ->
            sb.append(dependLib.variant)
            if (gioSdks.last() != dependLib) {
                sb.append("::")
            }
        }
        return sb.toString()
    }

}