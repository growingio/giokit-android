package com.growingio.giokit.hook

import android.net.Uri
import com.growingio.android.sdk.track.events.base.BaseEvent
import com.growingio.android.sdk.track.middleware.GEvent
import com.growingio.android.sdk.track.providers.EventBuilderProvider
import com.growingio.giokit.GioKitImpl
import com.growingio.giokit.launch.db.GioKitDbManager
import com.growingio.giokit.launch.db.GioKitEventBean
import org.json.JSONException

/**
 * <p>
 *
 * @author cpacm 2021/8/31
 */
object GioDatabase {

    /**
     * 将7天前的事件置为过期
     */
    @JvmStatic
    fun outdatedEvents() {
        if (GioKitImpl.inited) {
            GioKitDbManager.instance.outdatedEvents()
        }
    }

    @JvmStatic
    fun insertEvent(uri: Uri?, gEvent: GEvent) {
        if (uri == null) return
        val id: Long = try {
            uri.toString().split("/").last().toLong()
        } catch (e: NumberFormatException) {
            0
        }

        if (gEvent is BaseEvent) {
            val gioEvent = GioKitEventBean()
            gioEvent.gsid = if (id == 0L) gEvent.eventSequenceId else id
            gioEvent.status = GioKitEventBean.STATUS_READY
            gioEvent.time = gEvent.timestamp
            gioEvent.type = gEvent.eventType
            gioEvent.extra = gEvent.eventType

            try {
                val jsonObj = EventBuilderProvider.toJson(gEvent)
                gioEvent.data = jsonObj.toString()
                gioEvent.path = jsonObj.optString("path")
            } catch (e: JSONException) {
            }
            GioKitDbManager.instance.insertEvent(gioEvent)
        }
    }

    @JvmStatic
    fun deleteEvent(id: Long) {
        GioKitDbManager.instance.deleteEvent(id)
    }

    @JvmStatic
    fun removeEvents(lastId: Long, type: String) {
        GioKitDbManager.instance.removeEvents(lastId, type)
    }
}