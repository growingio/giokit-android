package com.growingio.giokit

import com.growingio.android.gmonitor.GMonitorOption

/**
 * <p>
 *
 * @author cpacm 2023/5/12
 */
data class GioKitOption(
    val attach: Boolean = true,
    val gmonitorOption: GMonitorOption? = null
)
