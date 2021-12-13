package com.growingio.giokit.plugin.utils

import com.growingio.giokit.plugin.base.KlassPool
import com.growingio.giokit.plugin.extension.GioKitExtension
import java.io.File
import java.lang.StringBuilder

interface GioTransformContext {
    val name: String

    /**
     * The project directory
     */
    val projectDir: File

    /**
     * The build directory
     */
    val buildDir: File

    /**
     * The temporary directory
     */
    val temporaryDir: File

    /**
     * The reports directory
     */
    val reportsDir: File

    /**
     * The boot classpath
     */
    val bootClasspath: Collection<File>

    /**
     * The compile classpath
     */
    val compileClasspath: Collection<File>

    /**
     * The runtime classpath
     */
    val runtimeClasspath: Collection<File>

    /**
     * The class pool
     */
    val klassPool: KlassPool

    /**
     * The application identifier
     */
    val applicationId: String

    /**
     * The buildType is debuggable
     */
    val isDebuggable: Boolean

    /**
     * is dataBinding enabled or not
     */
    val isDataBindingEnabled: Boolean

    /**
     * Check if has the specified property. Generally, the property is equivalent to project property
     *
     * @param name the name of property
     */
    fun hasProperty(name: String): Boolean

    /**
     * Returns the value of the specified property. Generally, the property is equivalent to project property
     *
     * @param name the name of property
     * @param default the default value
     */
    fun <T> getProperty(name: String, default: T): T = default

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

data class DependLib(val variant: String, val version: String)

