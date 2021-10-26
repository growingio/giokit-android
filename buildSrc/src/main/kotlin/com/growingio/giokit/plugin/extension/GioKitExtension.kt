package com.growingio.giokit.plugin.extension

import org.gradle.api.Action

/**
 * <p>
 *     插件配置
 * @author cpacm 2021/8/19
 */
open class GioKitExtension {

    //查找项目下的埋点代码位置
    var trackFinder: TrackFinder = TrackFinder()
    var debugMode: Boolean = false
    var enableIncremental = true
    var enableRelease = false

    fun trackFinder(action: Action<TrackFinder>) {
        action.execute(trackFinder)
    }
}

// 若用户没有设置，则不进行查找
open class TrackFinder {
    // 统计该域值下所有埋点信息，如 com.cpacm 表示统计 com.cpacm 包名下的埋点代码
    var domain: MutableList<String> = mutableListOf<String>()

    // 可选，查找手动埋点调用的代码
    var className: String = "com.growingio.android.sdk.autotrack.CdpAutotracker"
    var methodName: String = "trackCustomEvent"
}