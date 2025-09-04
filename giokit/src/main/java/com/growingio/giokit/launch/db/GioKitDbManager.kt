package com.growingio.giokit.launch.db

import android.os.Looper
import com.growingio.giokit.GioKitImpl
import com.growingio.giokit.instant.InstantEventCache
import com.growingio.giokit.utils.cleanOutDatedFile

/**
 * <p>
 *
 * @author cpacm 2021/8/30
 */
class GioKitDbManager private constructor() {

    private val EVENT_VALID_PERIOD_MILLS = 10L * 24 * 60 * 60000
    private val HTTP_VALID_PERIOD_MILLS = 24 * 60 * 60000L

    init {
        Looper.myQueue().addIdleHandler {
            try {
                //删除1天前的网络请求（网络请求数据只有24小时的有效期）
                GioKitDatabase.instance.getHttpDao().outdatedHttp(System.currentTimeMillis() - HTTP_VALID_PERIOD_MILLS)

                //删除7天前的事件缓存
                outdatedEvents()

                // 删除7天前的错误日志文件
                outdatedBreadcrumb()
                GioKitImpl.APPLICATION.cleanOutDatedFile(System.currentTimeMillis() - EVENT_VALID_PERIOD_MILLS)

            } catch (ignored: Exception) {
            }
            false
        }
    }

    /**************** Breadcrumb Database ****************/
    fun getBreadcrumb(id: Long): GioKitBreadCrumb {
        return GioKitDatabase.instance.getBreadcrumbDao().getBreadCrumb(id)
    }

    fun insertBreadcrumb(bc: GioKitBreadCrumb) {
        GioKitDatabase.instance.getBreadcrumbDao().insert(bc)
    }

    fun cleanBreadcrumb() {
        GioKitDatabase.instance.getBreadcrumbDao().clear()
    }

    fun outdatedBreadcrumb() {
        GioKitDatabase.instance.getBreadcrumbDao()
            .outdatedBreadCrumb(System.currentTimeMillis() - EVENT_VALID_PERIOD_MILLS)
    }

    fun getErrorBreadcrumbList(type: String, start: Int, pageSize: Int): List<GioKitBreadCrumb> {
        return GioKitDatabase.instance.getBreadcrumbDao().getBreadCrumbList(type, start, pageSize)
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
        return GioKitDatabase.instance.getHttpDao().sumHttpUploadData(GioKitImpl.launchTime) ?: 0L
    }

    suspend fun countRunningErrorRequest(): Int {
        return GioKitDatabase.instance.getHttpDao().countHttpErrorRequest(GioKitImpl.launchTime)
    }

    fun cleanHttp() {
        GioKitDatabase.instance.getHttpDao().clear()
    }


    /**************** Event Database ****************/
    fun getEvent(id: Int): GioKitEventBean {
        return GioKitDatabase.instance.getEventDao().getEvent(id)
    }

    fun insertEvent(event: GioKitEventBean) {
        InstantEventCache.acceptEvent(event)
        GioKitDatabase.instance.getEventDao().insert(event)
    }


    fun deleteEvent(id: Long) {
        GioKitDatabase.instance.getEventDao().updateFromGsid(id)
    }

    fun outdatedEvents() {
        GioKitDatabase.instance.getEventDao()
            .outdatedEvent(System.currentTimeMillis() - EVENT_VALID_PERIOD_MILLS)
    }

    // saas sdk 数据库中没有对照的gsid
    // 所以直接删除对应所有类型所有事件
    fun removeSaasEvents(type: String, lastId: String) {
        lastId.toLongOrNull()?.let { _ ->
            GioKitDatabase.instance.getEventDao().updateLastExtra(type)
        }
    }

    fun removeEvents(lastId: Long, extra: String) {
        GioKitDatabase.instance.getEventDao().updateLastGsid(lastId, extra)
    }

    fun updateEvents(lastId: Long, extra: String, newExtra: String) {
        GioKitDatabase.instance.getEventDao().updateLastGsidFailed(lastId, extra, newExtra)
    }

    fun getEventList(start: Int, pageSize: Int): List<GioKitEventBean> {
        return GioKitDatabase.instance.getEventDao().getEventList(start, pageSize)
    }

    fun getEventListByType(event: String, start: Int, pageSize: Int): List<GioKitEventBean> {
        return GioKitDatabase.instance.getEventDao().getEventListByType(event, start, pageSize)
    }

    fun cleanEvent() {
        GioKitDatabase.instance.getEventDao().clear()
    }


    companion object {
        val instance: GioKitDbManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            GioKitDbManager()
        }
    }
}