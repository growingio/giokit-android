package com.growingio.giokit.launch.sdkdata

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.growingio.giokit.hook.GioPluginConfig
import com.growingio.giokit.launch.db.GioKitDbManager
import com.growingio.giokit.launch.db.GioKitEventBean
import com.growingio.giokit.utils.MeasureUtils.timelineFormat

/**
 * <p>
 *
 * @author cpacm 2021/8/31
 */
class SdkDataSearchSource(val event: String) : PagingSource<Int, GioKitEventBean>() {

    override fun getRefreshKey(state: PagingState<Int, GioKitEventBean>): Int {
        return 0
    }


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GioKitEventBean> {
        val page = params.key ?: 0
        val start = params.loadSize * page
        val searchEvent = event.uppercase()
        return try {
            val dataList = arrayListOf<GioKitEventBean>()
            if (start == 0) {
                val tempData = GioKitDbManager.instance.getEventListByType(
                    searchEvent,
                    start,
                    params.loadSize
                )
                var tempDate: String? = null
                tempData.forEach { event ->
                    val date = timelineFormat(event.time)
                    if (tempDate != date) {
                        tempDate = date
                        val dateEvent = GioKitEventBean()
                        dateEvent.id = 0
                        dateEvent.type = tempDate
                        dataList.add(dateEvent)
                        dataList.add(event)
                    } else {
                        dataList.add(event)
                    }
                }
            } else {
                val tempData = GioKitDbManager.instance.getEventListByType(
                    searchEvent,
                    start - 1,
                    params.loadSize + 1
                )
                if (tempData.isNotEmpty()) {
                    var tempDate = timelineFormat(tempData[0].time)
                    for (index in 1 until tempData.size) {
                        val event = tempData[index]
                        val date = timelineFormat(event.time)
                        if (tempDate != date) {
                            tempDate = date
                            val dateEvent = GioKitEventBean()
                            dateEvent.id = 0
                            dateEvent.type = tempDate
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