package com.growingio.giokit.utils

import android.annotation.SuppressLint
import com.growingio.giokit.GioKitImpl
import java.text.SimpleDateFormat
import java.util.*

/**
 * <p>
 *
 * @author cpacm 2021/10/29
 */
object MeasureUtils {

    fun millis2FitTimeSpan(time: Long, region: Int = 5): String {
        var millis = time
        val units = arrayOf("天", "小时", "分", "秒", "毫秒")
        if (millis == 0L) return "0秒"
        val sb = StringBuilder()
        if (millis < 0) {
            sb.append("-")
            millis = -millis
        }
        val unitLen = intArrayOf(86400000, 3600000, 60000, 1000, 1)
        for (i in 0 until region) {
            if (millis >= unitLen[i]) {
                val mode = millis / unitLen[i]
                millis -= mode * unitLen[i]
                sb.append(mode).append(units[i])
            }
        }
        return sb.toString()
    }

    private val SDF_THREAD_LOCAL: ThreadLocal<MutableMap<String, SimpleDateFormat>> =
        object : ThreadLocal<MutableMap<String, SimpleDateFormat>>() {
            override fun initialValue(): MutableMap<String, SimpleDateFormat> {
                return hashMapOf()
            }
        }

    fun getDefaultTime(millis: Long): String {
        return getSafeDateTime(millis, "yyyy-MM-dd HH:mm:ss")
    }

    @SuppressLint("SimpleDateFormat")
    fun getSafeDateTime(millis: Long, pattern: String): String {
        val sdfMap = SDF_THREAD_LOCAL.get()!!
        var simpleDateFormat = sdfMap[pattern]
        if (simpleDateFormat == null) {
            simpleDateFormat = SimpleDateFormat(pattern)
            sdfMap[pattern] = simpleDateFormat
        }
        return simpleDateFormat.format(Date(millis))
    }

    /**
     * 得到时间  HH:mm
     */
    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime(curtime: Long, pattern: String = "yyyy-MM-dd HH:mm"): String? {
        var time: String? = null
        val sdfMap = SDF_THREAD_LOCAL.get()!!
        var simpleDateFormat = sdfMap[pattern]
        if (simpleDateFormat == null) {
            simpleDateFormat = SimpleDateFormat(pattern)
            sdfMap[pattern] = simpleDateFormat
        }
        val date = simpleDateFormat.format(curtime)
        val split = date.split("\\s".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (split.size > 1) {
            time = split[1]
        }
        return time
    }


    fun byte2FitMemorySize(byteSize: Long, precision: Int): String? {
        require(precision >= 0) { "precision shouldn't be less than zero!" }
        return if (byteSize < 0) {
            throw IllegalArgumentException("byteSize shouldn't be less than zero!")
        } else if (byteSize < MemoryConstants.KB) {
            String.format("%." + precision + "fB", byteSize.toDouble())
        } else if (byteSize < MemoryConstants.MB) {
            java.lang.String.format(
                "%." + precision + "fKB",
                byteSize.toDouble() / MemoryConstants.KB
            )
        } else if (byteSize < MemoryConstants.GB) {
            java.lang.String.format(
                "%." + precision + "fMB",
                byteSize.toDouble() / MemoryConstants.MB
            )
        } else {
            java.lang.String.format(
                "%." + precision + "fGB",
                byteSize.toDouble() / MemoryConstants.GB
            )
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
        val runningTime = GioKitImpl.launchTime
        if (mills >= runningTime) {
            return "运行期间"
        }
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

    fun getHttpMessage(httpCode: Int): String {
        if (HTTP_STATUS_CODE.containsKey(httpCode)) {
            return HTTP_STATUS_CODE[httpCode]!!
        }
        return "Error"
    }

    fun isHttpSuccessful(httpCode: Int): Boolean {
        return httpCode >= 200 && httpCode < 300
    }


    val HTTP_STATUS_CODE = hashMapOf<Int, String>(
        200 to "Ok",
        201 to "Created",
        202 to "Accepted",
        204 to "No Content",
        300 to "Multiple Choices",
        301 to "Moved Permanently",
        302 to "Found",
        303 to "See Other",
        304 to "Not Modified",
        307 to "Temporary Redirect",
        400 to "Bad Request",
        401 to "Unauthorized",
        403 to "Forbidden",
        404 to "Not Found",
        405 to "Method Not Allowed",
        409 to "Conflict",
        412 to "Precondition Failed",
        422 to "UnProcessable Entity",
        500 to "Server Error",
        502 to "Bad Gateway",
        503 to "Service Unavailable",
    )

    fun loadClass(clazz: String): Any? {
        try {
            return Class.forName(clazz)
        } catch (_: ClassNotFoundException) {
        } catch (_: UnsatisfiedLinkError) {
        } catch (_: Throwable) {
        }
        return null
    }
}

object MemoryConstants {
    const val BYTE = 1
    const val KB = 1024
    const val MB = 1048576
    const val GB = 1073741824
}