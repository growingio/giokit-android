package com.growingio.giokit.launch.sdkhttp

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.growingio.giokit.launch.db.GioKitDbManager
import com.growingio.giokit.launch.db.GioKitHttpBean
import com.growingio.giokit.utils.MeasureUtils.timelineFormat

/**
 * <p>
 *
 * @author cpacm 2021/10/31
 */
class SdkHttpSource : PagingSource<Int, GioKitHttpBean>() {

    override fun getRefreshKey(state: PagingState<Int, GioKitHttpBean>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GioKitHttpBean> {
        val page = params.key ?: 0
        val start = params.loadSize * page

        return try {
            val dataList = arrayListOf<GioKitHttpBean>()
            if (start == 0) {
                val tempData = GioKitDbManager.instance.getHttpList(start, params.loadSize)
                var tempDate: String? = null
                tempData.forEach { event ->
                    val date = timelineFormat(event.httpTime)
                    if (tempDate != date) {
                        tempDate = date
                        val dateEvent = GioKitHttpBean()
                        dateEvent.id = 0
                        dateEvent.requestMethod = tempDate
                        dataList.add(dateEvent)
                        dataList.add(event)
                    } else {
                        dataList.add(event)
                    }
                }
            } else {
                val tempData = GioKitDbManager.instance.getHttpList(start - 1, params.loadSize + 1)
                if (tempData.isNotEmpty()) {
                    var tempDate = timelineFormat(tempData[0].httpTime)
                    for (index in 1 until tempData.size) {
                        val event = tempData[index]
                        val date = timelineFormat(event.httpTime)
                        if (tempDate != date) {
                            tempDate = date
                            val dateEvent = GioKitHttpBean()
                            dateEvent.id = 0
                            dateEvent.requestMethod = tempDate
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