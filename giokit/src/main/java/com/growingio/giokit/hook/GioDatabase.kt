package com.growingio.giokit.hook

import android.net.Uri
import com.growingio.android.sdk.track.events.base.BaseEvent
import com.growingio.android.sdk.track.middleware.GEvent
import com.growingio.android.sdk.track.providers.EventBuilderProvider
import com.growingio.giokit.GioKitImpl
import com.growingio.giokit.launch.db.GioKitDbManager
import com.growingio.giokit.launch.db.GioKitEventBean
import org.json.JSONException
import org.json.JSONObject

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
        val id: Long = if (uri == null) 0 else try {
            uri.toString().split("/").last().toLong()
        } catch (e: NumberFormatException) {
            0
        }

        if (gEvent is BaseEvent) {
            val gioEvent = GioKitEventBean()
            gioEvent.gsid = if (id == 0L) gEvent.eventSequenceId else id
            gioEvent.status = if (uri != null) GioKitEventBean.STATUS_READY else GioKitEventBean.STATUS_DROP
            gioEvent.time = gEvent.timestamp
            gioEvent.type = gEvent.eventType
            gioEvent.extra = gEvent.eventType

            try {
                val jsonObj = EventBuilderProvider.toJson(gEvent)
                gioEvent.data = minimizeJsonAttribute(jsonObj)
                gioEvent.path = jsonObj.optString("path")
            } catch (ignored: Exception) {
            }
            GioKitDbManager.instance.insertEvent(gioEvent)
        }
    }

    private fun minimizeJsonAttribute(jsonObj: JSONObject): String {
        return try {
            val dataSize = jsonObj.toString().toByteArray().size
            if (dataSize > 2000_000) {
                val jsonObject = JSONObject()
                jsonObject.put("\$data_over_limit\$", dataSize.toString())
                jsonObj.put("attributes", jsonObject)
            }
            jsonObj.toString()
        } catch (e: JSONException) {
            "json data error"
        } catch (e: Exception) {
            "data error"
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