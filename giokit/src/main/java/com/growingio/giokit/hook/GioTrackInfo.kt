@file:Suppress("UNCHECKED_CAST")

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
    fun inject(list: Set<String>) {
        trackList.clear()
        trackList.addAll(list)
        Log.d("GioTrackInfo", trackList.toString())
    }

    //由插件注入埋点代码位置
    fun initGioTrack() {
        try {
            val clazz = Class.forName("com.growingio.giokit.GioCode")
            val obj = clazz.getConstructor().newInstance()
            if (obj is Set<*>) {
                inject(obj as Set<String>)
            }
        } catch (ignored: ClassNotFoundException) {
            Log.e("GioTrackInfo", "don't find GioCode", ignored)
        }
    }
}