package com.growingio.giokit.hook

import android.net.UrlQuerySanitizer
import android.util.Log
import com.growingio.giokit.launch.db.GioKitDbManager
import com.growingio.giokit.launch.db.GioKitHttpBean
import com.growingio.snappy.XORUtils
import org.iq80.snappy.Snappy
import java.lang.Exception
import java.net.HttpURLConnection

/**
 * <p>
 *
 * @author cpacm 2021/10/27
 */
object GioHttp {

    /**
     * 在okhttp中添加拦截器获得请求数据
     * 已经直接在编译期加入 GioHttpCaptureInterceptor
     */
    @JvmStatic
    fun addGioKitIntercept() {
    }

    @JvmStatic
    fun parseGioKitUrlConnection(
        urlConnection: HttpURLConnection,
        headers: Map<String, String>,
        data: ByteArray?
    ) {
        val statusCode = urlConnection.responseCode
        if (data == null || statusCode / 100 == 3) return
        val gioHttp = GioKitHttpBean()
        val httpUrl = urlConnection.url
        gioHttp.httpTime = (parseURLQuery(httpUrl.toString(), "stm")).toLong()
        gioHttp.requestMethod = urlConnection.requestMethod
        gioHttp.requestUrl = httpUrl.toString()
        gioHttp.requestSize = data.size.toLong()
        val requestHeaderSb = StringBuilder()
        headers.forEach {
            requestHeaderSb.append(it.key).append(": ").append(it.value).append("\n")
        }
        gioHttp.requestHeader = requestHeaderSb.toString()
        if (bodyHasSnappyEncoding(headers)) {
            val compressedOut = XORUtils.encrypt(data, (gioHttp.httpTime and 0xFF).toInt())
            gioHttp.requestBody = String(Snappy.uncompress(compressedOut, 0, compressedOut.size))
        } else {
            gioHttp.requestBody = String(data)
        }

        gioHttp.responseCode = urlConnection.responseCode
        val responseHeaderSb = StringBuilder()
        var receivedMillis = 0L
        var sentMillis = 0L
        urlConnection.headerFields.forEach {
            try {
                if (it.key == null) {
                    responseHeaderSb.append(it.value.first()).append("\n")
                    return@forEach
                }
                if (it.key == "X-Android-Received-Millis") {
                    receivedMillis = it.value.first().toLong()
                    return@forEach
                }
                if (it.key == "X-Android-Sent-Millis") {
                    sentMillis = it.value.first().toLong()
                    return@forEach
                }
                responseHeaderSb.append(it.key).append(": ").append(it.value.first()).append("\n")
            } catch (e: Exception) {
                Log.e("GioHttp", "parse response header error")
            }
        }
        if (sentMillis != 0L && receivedMillis != 0L) {
            gioHttp.httpCost = receivedMillis - sentMillis
        } else {
            gioHttp.httpCost = System.currentTimeMillis() - gioHttp.httpTime
        }
        gioHttp.responseHeader = responseHeaderSb.toString()
        gioHttp.responseUrl = httpUrl.toString()
        gioHttp.responseBody = "No Content"
        gioHttp.responseSize = urlConnection.contentLength.toLong()
        GioKitDbManager.instance.insertHttp(gioHttp)
        Log.d("Http", gioHttp.toString())
    }

    private fun bodyHasSnappyEncoding(headers: Map<String, String>): Boolean {
        val contentCrypt = headers["X-Crypt-Codec"] ?: return false
        val contentCompress = headers["X-Compress-Codec"] ?: return false
        return contentCrypt.equals("1", ignoreCase = true) &&
                contentCompress.equals("2", ignoreCase = true)
    }

    private fun parseURLQuery(url: String, key: String): String {
        val uqs = UrlQuerySanitizer(url)
        return uqs.getValue(key) ?: "0"
    }
}