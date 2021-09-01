package com.growingio.giokit.launch.db

import android.content.ContentResolver
import com.growingio.android.sdk.track.events.base.BaseEvent
import com.growingio.android.sdk.track.middleware.EventsInfoTable
import com.growingio.android.sdk.track.middleware.GEvent
import com.growingio.android.sdk.track.middleware.Serializer
import com.growingio.giokit.GioKitImpl
import java.io.IOException

/**
 * <p>
 *
 * @author cpacm 2021/8/30
 */
class GioKitDbManager private constructor() {

    private val EVENT_VALID_PERIOD_MILLS = 7L * 24 * 60 * 60000
    private var eventsInfoAuthority: String? = null

    init {
        eventsInfoAuthority = GioKitImpl.APPLICATION.packageName + ".EventsContentProvider"
    }

    fun insertEvent(event: GEvent) {
        if (event is BaseEvent) {
            val gioEvent = GioKitEventBean()
            gioEvent.data = event.toJSONObject().toString()
            gioEvent.gsid = event.globalSequenceId
            gioEvent.status = GioKitEventBean.STATUS_READY
            gioEvent.time = event.timestamp
            gioEvent.type = event.eventType

            val jsonObj = event.toJSONObject()
            gioEvent.path = jsonObj.optString("path")

            GioKitDatabase.instance.getEventDao().insert(gioEvent)
        }
    }

    fun deleteEvent(id: Long) {
        try {
            val contentResolver: ContentResolver = GioKitImpl.APPLICATION.getContentResolver()
            val uri = EventsInfoTable.getContentUri()
            val sql =
                "SELECT * FROM " + EventsInfoTable.TABLE_EVENTS + " WHERE " + EventsInfoTable.COLUMN_ID + "=" + id
            val cursor = contentResolver.query(uri, null, sql, null, "rawQuery")
            if (cursor?.moveToFirst() == true) {
                val data = cursor.getBlob(cursor.getColumnIndex(EventsInfoTable.COLUMN_DATA))
                val event = unpack(data)
                if (event != null && event is BaseEvent) {
                    GioKitDatabase.instance.getEventDao().updateFromGsid(event.globalSequenceId)
                }
            }
            cursor?.close()
        } catch (e: IOException) {

        }
    }

    fun outdatedEvents() {
        GioKitDatabase.instance.getEventDao()
            .outdatedEvent(System.currentTimeMillis() - EVENT_VALID_PERIOD_MILLS)
    }

    fun removeEvents(lastId: Long) {
        try {
            val contentResolver: ContentResolver = GioKitImpl.APPLICATION.getContentResolver()
            val uri = EventsInfoTable.getContentUri()
            val sql =
                "SELECT * FROM " + EventsInfoTable.TABLE_EVENTS + " WHERE " + EventsInfoTable.COLUMN_ID + "=" + lastId
            val cursor = contentResolver.query(uri, null, sql, null, "rawQuery")
            if (cursor?.moveToFirst() == true) {
                val data = cursor.getBlob(cursor.getColumnIndex(EventsInfoTable.COLUMN_DATA))
                val event = unpack(data)
                if (event != null && event is BaseEvent) {
                    GioKitDatabase.instance.getEventDao().updateLastGsid(event.globalSequenceId)
                }
            }
            cursor?.close()
        } catch (e: IOException) {

        }
    }

    private fun unpack(data: ByteArray): GEvent? {
        try {
            return Serializer.objectDeserialization(data)
        } catch (e: IOException) {
        } catch (e: ClassNotFoundException) {
        }
        return null
    }


    fun getDataList(start: Int, pageSize: Int): List<GioKitEventBean> {
        return GioKitDatabase.instance.getEventDao().getEventList(start, pageSize)
    }


    companion object {
        val instance: GioKitDbManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            GioKitDbManager()
        }
    }
}