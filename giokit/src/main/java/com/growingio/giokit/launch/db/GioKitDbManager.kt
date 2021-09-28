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

    fun insertEvent(event: GioKitEventBean) {
        GioKitDatabase.instance.getEventDao().insert(event)
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

    // saas sdk 数据库中没有对照的gsid
    // 所以直接删除对应所有类型所有事件
    fun removeEvents(type: String, lastId: String) {
        lastId.toLongOrNull()?.let { id ->
            GioKitDatabase.instance.getEventDao().updateLastExtra(type)
        }
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