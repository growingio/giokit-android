package com.growingio.giokit.plugin.utils

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project
import org.objectweb.asm.tree.ClassNode

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

inline fun <reified T : BaseExtension> Project.getAndroid(): T = extensions.getByName("android") as T

var DEBUG_ENABLE = false

fun String.println() {
    if (DEBUG_ENABLE) {
        println("[Giokit.Info] $this")
    }
}

val ClassNode.className: String
    get() = name.replace('/', '.')

val String.formatDot: String
    get() = this.replace('/', '.')

fun String.ignoreClass(context: GioTransformContext): Boolean {
    for (domain in context.gioConfig.trackFinder.domain) {
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

// find jar manifest ==> define in gradle >> jar{"giokit-plugin-version":version}
//fun Any.getGiokitPluginVersion(): String {
//    try {
//        val jarPath = URLDecoder.decode(
//            File(this.javaClass.protectionDomain.codeSource.location.path).canonicalPath,
//            Charset.defaultCharset()
//        )
//        val inputStream = JarInputStream(FileInputStream(jarPath))
//        val pluginVersion = inputStream.manifest.mainAttributes.getValue("giokit-plugin-version")
//        if (pluginVersion.isNullOrEmpty()) {
//            throw Exception("Can't find Giokit plugin!!")
//        }
//        return pluginVersion
//    } catch (e: Exception) {
//        throw Exception("Can't find Giokit plugin!!")
//    }
//}

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
