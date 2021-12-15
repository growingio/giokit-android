package com.growingio.giokit.plugin.utils

import com.android.build.gradle.api.BaseVariant
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.io.FileInputStream
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.jar.JarInputStream

/**
 * <p>
 *
 * @author cpacm 2021/8/17
 */

fun BaseVariant.isRelease(): Boolean {
    if (this.name.contains("release") || this.name.contains("Release")) {
        return true
    }
    return false
}

fun String.println() {
    if (isLogEnable()) {
        println("[giokit plugin]===>$this")
    }
}

var GIOKIT_LOG_ENABLE = false

fun isLogEnable(): Boolean {
    return GIOKIT_LOG_ENABLE
}

val ClassNode.className: String
    get() = name.replace('/', '.')

val String.formatDot: String
    get() = this.replace('/', '.')

fun String.ignoreClass(context: GioTransformContext): Boolean {
    for (domain in context.gioConfig.gioKitExt.trackFinder.domain) {
        if (this.startsWith(domain, true)) {
            return false
        }
    }

    for (ignore in ignoreClassNames) {
        if (this.startsWith(ignore, true)) {
            return true
        }
    }
    return false
}

fun String.isAssignableFrom(context: GioTransformContext, parentClass: Class<*>): Boolean {
    try {
        val findClass = context.klassPool.classLoader.loadClass(this)
        return parentClass.isAssignableFrom(findClass)
    } catch (e: ClassNotFoundException) {
    } catch (e: NoClassDefFoundError) {
    }
    return false
}

fun String.loadClass(context: GioTransformContext): Class<*>? {
    try {
        return context.klassPool.classLoader.loadClass(this)
    } catch (e: ClassNotFoundException) {
    } catch (e: NoClassDefFoundError) {
    }
    return null
}

// find jar manifest ==> define in gradle >> jar{"giokit-plugin-version":version}
fun Any.getGiokitPluginVersion(): String {
    try {
        val jarPath = URLDecoder.decode(
            File(this.javaClass.protectionDomain.codeSource.location.path).canonicalPath,
            Charset.defaultCharset()
        )
        val inputStream = JarInputStream(FileInputStream(jarPath))
        val pluginVersion = inputStream.manifest.mainAttributes.getValue("giokit-plugin-version")
        if (pluginVersion.isNullOrEmpty()) {
            throw Exception("Can't find Giokit plugin!!")
        }
        return pluginVersion
    } catch (e: Exception) {
        throw Exception("Can't find Giokit plugin!!")
    }
}

val ignoreClassNames = arrayListOf(
    "kotlin",
    "android",
    "com.growingio",
    "androidx",
    "com.google",
    "okhttp3",
    "okio",
    "com.github.ybq.android",
    "io.mattcarroll.hover",
    "org.intellij"
)
