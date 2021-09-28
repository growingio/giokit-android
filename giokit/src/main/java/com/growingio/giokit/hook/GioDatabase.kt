package com.growingio.giokit.hook

import com.growingio.android.sdk.track.events.base.BaseEvent
import com.growingio.android.sdk.track.middleware.GEvent
import com.growingio.giokit.launch.db.GioKitDatabase
import com.growingio.giokit.launch.db.GioKitDbManager
import com.growingio.giokit.launch.db.GioKitEventBean
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * <p>
 *
 * @author cpacm 2021/8/31
 */
object GioDatabase {

    /**
     * 插入2.0数据
     */
    @JvmStatic
    fun insertSaasEvent(data: String) {
        val gioEvent = GioKitEventBean()
        gioEvent.data = data
        gioEvent.status = GioKitEventBean.STATUS_READY
        try {
            val jsonObj = JSONObject(data)
            var gsid = jsonObj.optInt("gesid")
            if (gsid == 0) {
                val eArry = jsonObj.optJSONArray("e") ?: JSONArray()
                if (eArry.length()> 0) {
                    gsid = eArry.getJSONObject(0).optInt("gesid")
                }
            }
            gioEvent.time = jsonObj.optLong("tm")
            gioEvent.gsid = gsid.toLong()
            val type = jsonObj.optString("t")
            gioEvent.type = type
            gioEvent.path = jsonObj.optString("p")
            gioEvent.extra = getSaasEventType(type)
        } catch (e: JSONException) {
        }
        GioKitDbManager.instance.insertEvent(gioEvent)
    }

    private fun getSaasEventType(type: String): String {
        return when (type) {
            "activate", "reengage" -> "ctvt"
            "cstm", "pvar", "evar", "ppl", "vstr" -> "cstm"
            "page", "vst", "cls" -> "pv"
            "clck", "chng" -> "other"
            "imp" -> "imp"
            else -> "empty"
        }
    }

    @JvmStatic
    fun removeSaasEvents(extra: String, lastId: String) {
        GioKitDbManager.instance.removeEvents(extra, lastId)
    }

    @JvmStatic
    fun insertV3Event(gEvent: GEvent) {
        if (gEvent is BaseEvent) {
            val gioEvent = GioKitEventBean()
            gioEvent.gsid = gEvent.globalSequenceId
            gioEvent.status = GioKitEventBean.STATUS_READY
            gioEvent.time = gEvent.timestamp
            gioEvent.type = gEvent.eventType
            gioEvent.extra = gEvent.eventType

            try {
                val jsonObj = gEvent.toJSONObject()
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