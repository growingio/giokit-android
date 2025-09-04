package com.growingio.giokit.hook

import android.net.Uri
import com.growingio.android.sdk.track.events.AutotrackEventType
import com.growingio.android.sdk.track.events.TrackEventType
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
            // INSTANT, AUTOTRACK, TRACK, OTHER, UNDELIVERED
            gioEvent.extra = getDatabaseEventType(gEvent)

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
        // type in INSTANT, AUTOTRACK, TRACK, OTHER, UNDELIVERED
        GioKitDbManager.instance.removeEvents(lastId, type)
    }

    @JvmStatic
    fun delayEvents(lastId: Long, type: String) {
        // type in INSTANT, AUTOTRACK, TRACK, OTHER, UNDELIVERED
        if (type == UNDELIVERED_EVENT_TYPE) {
            return
        }
        GioKitDbManager.instance.updateEvents(lastId, type, UNDELIVERED_EVENT_TYPE)
    }

    private fun getDatabaseEventType(gEvent: GEvent): String {
        val eventType = gEvent.eventType
        return when (eventType) {
            TrackEventType.VISIT, TrackEventType.ACTIVATE, TrackEventType.REENGAGE -> INSTANT_EVENT_TYPE
            AutotrackEventType.PAGE, AutotrackEventType.PAGE_ATTRIBUTES, AutotrackEventType.VIEW_CLICK, AutotrackEventType.VIEW_CHANGE -> AUTOTRACK_EVENT_TYPE
            TrackEventType.CUSTOM, TrackEventType.VISITOR_ATTRIBUTES, TrackEventType.LOGIN_USER_ATTRIBUTES, TrackEventType.CONVERSION_VARIABLES -> TRACK_EVENT_TYPE
            else -> OTHER_EVENT_TYPE
        }
    }

    private const val INSTANT_EVENT_TYPE: String = "INSTANT"
    private const val AUTOTRACK_EVENT_TYPE: String = "AUTOTRACK"
    private const val TRACK_EVENT_TYPE: String = "TRACK"
    private const val OTHER_EVENT_TYPE: String = "OTHER"
    private const val UNDELIVERED_EVENT_TYPE: String = "UNDELIVERED"
}