package com.growingio.giokit.utils

import com.growingio.android.sdk.track.events.EventFilterInterceptor

/**
 * <p>
 *     Event filter proxy. Exclude Giokit activity
 * @author cpacm 2022/8/10
 */
class GiokitEventFilterProxy(val eventFilterInterceptor: EventFilterInterceptor) :
    EventFilterInterceptor by eventFilterInterceptor {

    override fun filterEventPath(path: String): Boolean {
        if (path.equals("/UniversalActivity") || path.equals("/GiokitSettingActivity")) return false
        return eventFilterInterceptor.filterEventPath(path)
    }
}