package com.growingio.giokit.launch.sdkpref

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.growingio.giokit.launch.db.*

/**
 * <p>
 *
 * @author cpacm 2022/10/23
 */
class SdkPrefSource : PagingSource<Int, GioKitBreadCrumb>() {

    override fun getRefreshKey(state: PagingState<Int, GioKitBreadCrumb>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GioKitBreadCrumb> {
        val page = params.key ?: 0
        val start = params.loadSize * page

        return try {
            val dataList = arrayListOf<GioKitBreadCrumb>()
            if (start == 0) {
                val dateEvent = GioKitBreadCrumb()
                dateEvent.id = 0
                dateEvent.extra = "Launching"
                dataList.add(dateEvent)
            }
            val tempData =
                GioKitDbManager.instance.getErrorBreadcrumbList(BREADCRUMB_TYPE_PERFORMANCE, start, params.loadSize)
            tempData.forEach { event ->
                dataList.add(event)
                if(event.category == "APP"){
                    val dateEvent = GioKitBreadCrumb()
                    dateEvent.id = 0
                    dateEvent.extra = "Launch"
                    dataList.add(dateEvent)
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