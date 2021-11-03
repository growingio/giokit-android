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
    private val HTTP_VALID_PERIOD_MILLS = 24 * 60 * 60000L

    init {
        //删除1天前的网络请求（网络请求数据只有24小时的有效期）
        GioKitDatabase.instance.getHttpDao()
            .outdatedHttp(System.currentTimeMillis() - HTTP_VALID_PERIOD_MILLS)
    }

    /**************** Http Database ****************/
    fun getHttp(id: Int): GioKitHttpBean {
        return GioKitDatabase.instance.getHttpDao().getHttp(id)
    }

    fun insertHttp(http: GioKitHttpBean) {
        GioKitDatabase.instance.getHttpDao().insert(http)
    }

    fun getHttpList(start: Int, pageSize: Int): List<GioKitHttpBean> {
        return GioKitDatabase.instance.getHttpDao().getHttpList(start, pageSize)
    }

    suspend fun countRunningRequest(): Int {
        return GioKitDatabase.instance.getHttpDao().countHttpRequest(GioKitImpl.launchTime)
    }

    suspend fun sumUploadDataSize(): Long {
        return GioKitDatabase.instance.getHttpDao().sumHttpUploadData(GioKitImpl.launchTime)
    }

    suspend fun countRunningErrorRequest(): Int {
        return GioKitDatabase.instance.getHttpDao().countHttpErrorRequest(GioKitImpl.launchTime)
    }


    /**************** Event Database ****************/
    fun getEvent(id: Int): GioKitEventBean {
        return GioKitDatabase.instance.getEventDao().getEvent(id)
    }

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
        lastId.toLongOrNull()?.let { _ ->
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


    fun getEventList(start: Int, pageSize: Int): List<GioKitEventBean> {
        return GioKitDatabase.instance.getEventDao().getEventList(start, pageSize)
    }

    fun getEventListByType(event: String, start: Int, pageSize: Int): List<GioKitEventBean> {
        return GioKitDatabase.instance.getEventDao().getEventListByType(event, start, pageSize)
    }


    companion object {
        val instance: GioKitDbManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            GioKitDbManager()
        }
    }
}