package com.growingio.giokit.hook

import com.growingio.android.sdk.track.middleware.GEvent
import com.growingio.giokit.launch.db.GioKitDbManager

/**
 * <p>
 *
 * @author cpacm 2021/8/31
 */
object GioDatabase {

    @JvmStatic
    fun insertEvent(gEvent: GEvent) {
        GioKitDbManager.instance.insertEvent(gEvent)
    }

    @JvmStatic
    fun deleteEvent(id: Long) {
        GioKitDbManager.instance.deleteEvent(id)
    }

    /**
     * 将7天前的事件置为过期
     */
    @JvmStatic
    fun outdatedEvents() {
        GioKitDbManager.instance.outdatedEvents()
    }

    @JvmStatic
    fun removeEvents(lastId: Long) {
        GioKitDbManager.instance.removeEvents(lastId)
    }


}