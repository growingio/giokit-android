package com.growingio.giokit.hook

import android.util.Log

/**
 * <p>
 *     由插件获取的app所有手动埋点位置
 * @author cpacm 2021/8/23
 */
object GioTrackInfo {

    val trackList = hashSetOf<String>()

    @JvmStatic
    fun inject(list: List<String>) {
        trackList.clear()
        trackList.addAll(list)
        Log.d("GioTrackInfo", trackList.toString())
    }
}