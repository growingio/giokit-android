package com.growingio.giokit.hook

import android.net.UrlQuerySanitizer
import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.VolleyError
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

    private fun parseVolleyMethod(method: Int): String {
        return when (method) {
            Request.Method.GET -> "GET"
            Request.Method.POST -> "POST"
            Request.Method.PUT -> "PUT"
            Request.Method.DELETE -> "DELETE"
            Request.Method.HEAD -> "HEAD"
            Request.Method.OPTIONS -> "OPTIONS"
            Request.Method.PATCH -> "PATCH"
            Request.Method.TRACE -> "TRACE"
            else -> "UNKNOWN"
        }
    }

    @JvmStatic
    fun parseGioKitVolleySuccess(request: Request<ByteArray>, networkResponse: NetworkResponse) {
        val gioHttp = GioKitHttpBean()
        val httpUrl = request.url
        gioHttp.requestUrl = httpUrl
        gioHttp.httpTime = (parseURLQuery(httpUrl, "stm")).toLong()
        val requestHeaderSb = StringBuilder()
        request.headers.forEach {
            requestHeaderSb.append(it.key).append(": ").append(it.value).append("\n")
        }
        gioHttp.requestSize = request.body.size.toLong()
        gioHttp.requestHeader = requestHeaderSb.toString()
        if (bodyHasSnappyEncoding(request.headers)) {
            val compressedOut = XORUtils.encrypt(request.body, (gioHttp.httpTime and 0xFF).toInt())
            gioHttp.requestBody = String(Snappy.uncompress(compressedOut, 0, compressedOut.size))
        } else {
            gioHttp.requestBody = String(request.body)
        }
        gioHttp.requestMethod = parseVolleyMethod(request.method)

        gioHttp.responseCode = networkResponse.statusCode
        gioHttp.responseUrl = httpUrl
        val responseHeaderSb = StringBuilder()
        networkResponse.headers?.forEach {
            responseHeaderSb.append(it.key).append(": ").append(it.value).append("\n")
        }
        gioHttp.responseHeader = responseHeaderSb.toString()
        if (networkResponse.data == null) {
            gioHttp.responseBody = "No Content"
        } else {
            gioHttp.responseBody = String(networkResponse.data)
        }
        gioHttp.responseSize = networkResponse.data.size.toLong()
        if (networkResponse.networkTimeMs != 0L) {
            gioHttp.httpCost = networkResponse.networkTimeMs
        } else {
            gioHttp.httpCost = System.currentTimeMillis() - gioHttp.httpTime
        }
        GioKitDbManager.instance.insertHttp(gioHttp)
        Log.d("Http", gioHttp.toString())
    }

    @JvmStatic
    fun parseGioKitVolleyError(request: Request<ByteArray>, error: VolleyError) {
        if (error.networkResponse == null) return
        parseGioKitVolleySuccess(request, error.networkResponse)
    }
}