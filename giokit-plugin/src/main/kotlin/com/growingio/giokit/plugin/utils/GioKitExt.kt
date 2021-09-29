package com.growingio.giokit.plugin.utils

import com.android.build.gradle.api.BaseVariant
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
