package com.growingio.giokit.launch.sdkcrash

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.growingio.giokit.launch.db.BREADCRUMB_TYPE_ERROR
import com.growingio.giokit.launch.db.GioKitBreadCrumb
import com.growingio.giokit.launch.db.GioKitDbManager
import com.growingio.giokit.launch.db.GioKitHttpBean
import com.growingio.giokit.utils.MeasureUtils.timelineFormat

/**
 * <p>
 *
 * @author cpacm 2022/10/23
 */
class SdkErrorSource : PagingSource<Int, GioKitBreadCrumb>() {

    override fun getRefreshKey(state: PagingState<Int, GioKitBreadCrumb>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GioKitBreadCrumb> {
        val page = params.key ?: 0
        val start = params.loadSize * page

        return try {
            val dataList = arrayListOf<GioKitBreadCrumb>()
            if (start == 0) {
                val tempData =
                    GioKitDbManager.instance.getErrorBreadcrumbList(BREADCRUMB_TYPE_ERROR, start, params.loadSize)
                var tempDate: String? = null
                tempData.forEach { event ->
                    val date = timelineFormat(event.time)
                    if (tempDate != date) {
                        tempDate = date
                        val dateEvent = GioKitBreadCrumb()
                        dateEvent.id = 0
                        dateEvent.extra = tempDate!!
                        dataList.add(dateEvent)
                        dataList.add(event)
                    } else {
                        dataList.add(event)
                    }
                }
            } else {
                val tempData = GioKitDbManager.instance.getErrorBreadcrumbList(BREADCRUMB_TYPE_ERROR,start - 1, params.loadSize + 1)
                if (tempData.isNotEmpty()) {
                    var tempDate = timelineFormat(tempData[0].time)
                    for (index in 1 until tempData.size) {
                        val event = tempData[index]
                        val date = timelineFormat(event.time)
                        if (tempDate != date) {
                            tempDate = date
                            val dateEvent = GioKitBreadCrumb()
                            dateEvent.id = 0
                            dateEvent.extra = tempDate
                            dataList.add(dateEvent)
                            dataList.add(event)
                        } else {
                            dataList.add(event)
                        }
                    }
                }
            }
            LoadResult.Page(
                dataList,
                if (page <= 0) null else page - 1,
                if (dataList.size >= params.loadSize) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}