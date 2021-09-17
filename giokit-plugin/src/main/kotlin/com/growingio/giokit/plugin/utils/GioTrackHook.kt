package com.growingio.giokit.plugin.utils

data class GioTrackHook(
    val className: String,
    val methodName: String,
    var selfClassName: String = "",
    var selfMethodName: String = ""
)