package com.growingio.giokit.launch.sdkdata

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.growingio.giokit.launch.db.GioKitDbManager
import com.growingio.giokit.launch.db.GioKitEventBean
import java.text.SimpleDateFormat
import java.util.*

/**
 * <p>
 *
 * @author cpacm 2021/8/31
 */
class SdkDataSource : PagingSource<Int, GioKitEventBean>() {

    override fun getRefreshKey(state: PagingState<Int, GioKitEventBean>): Int {
        return 0
    }


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GioKitEventBean> {
        val page = params.key ?: 0
        val start = params.loadSize * page

        return try {
            val dataList = arrayListOf<GioKitEventBean>()
            if (start == 0) {
                val tempData = GioKitDbManager.instance.getDataList(start, params.loadSize)
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
                val tempData = GioKitDbManager.instance.getDataList(start - 1, params.loadSize + 1)
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

    val todayTime: Long
        get() {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

    fun timelineFormat(mills: Long): String {
        val time = todayTime
        if (mills > time) {
            return "今日"
        }
        if (mills > yesterdayTime) {
            return "昨日"
        }
        return formatTime(mills, "MM月dd日")
    }

    /**
     * 获取昨天0时的时间戳
     *
     * @return
     */
    val yesterdayTime: Long
        get() = todayTime - 24 * 3600 * 1000

    fun formatTime(time: Long, pattern: String): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(time)
    }

}