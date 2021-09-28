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

    //v3
    var v3Domain: String = ""
    var v3XmlScheme: String = ""
    var gioV3Sdks = mutableSetOf<DependLib>()
    var hasGioPluginV3 = false

    //saas
    var saasDomain: String = ""
    var saasXmlScheme: String = ""
    var gioSaasSdks = mutableSetOf<DependLib>()
    var hasGioPluginSaas = false


    var gioTracks = mutableSetOf<GioTrackHook>()
    var gioKitExt = GioKitExtension()
        private set

    fun setPluginExtension(ext: GioKitExtension) {
        this.gioKitExt = ext
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