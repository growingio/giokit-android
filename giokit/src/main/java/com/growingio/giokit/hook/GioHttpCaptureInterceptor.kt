package com.growingio.giokit.hook

import android.os.Build
import androidx.annotation.RequiresApi
import com.growingio.giokit.launch.db.GioKitDbManager
import com.growingio.giokit.launch.db.GioKitHttpBean
import com.growingio.snappy.XORUtils
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import okio.GzipSource
import org.iq80.snappy.Snappy
import java.io.EOFException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

/**
 * <p>
 *     由 {@link GioHttpTransformer} 在编译中注入至 {@link OkHttpClient} 中
 * @author cpacm 2021/10/27
 */
class GioHttpCaptureInterceptor @JvmOverloads constructor() : Interceptor {

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun intercept(chain: Interceptor.Chain): Response {
        val gioHttp = GioKitHttpBean()
        val request = chain.request()
        val requestBody = request.body()
        val connection = chain.connection()
        var requestStartMessage =
            ("--> ${request.method()} ${request.url()}${if (connection != null) " " + connection.protocol() else ""}")
        if (requestBody != null) {
            requestStartMessage += " (${requestBody.contentLength()}-byte body)"
        }
        gioHttp.httpTime = (request.url().queryParameter("stm") ?: "0").toLong()
        gioHttp.requestMethod = request.method()
        gioHttp.requestUrl = request.url().toString()
        gioHttp.requestSize = requestBody?.contentLength() ?: 0L
        log(requestStartMessage)

        val requestHeaderSb = StringBuilder()

        val headers = request.headers()
        if (requestBody != null) {
            requestBody.contentType()?.let {
                if (headers["Content-Type"] == null) {
                    requestHeaderSb.append("Content-Type: $it\n")
                    log("Content-Type: $it")
                }
            }
            if (requestBody.contentLength() != -1L) {
                if (headers["Content-Length"] == null) {
                    requestHeaderSb.append("Content-Length: ${requestBody.contentLength()}\n")
                    log("Content-Length: ${requestBody.contentLength()}")
                }
            }
        }

        for (i in 0 until headers.size()) {
            requestHeaderSb.append(headers.name(i)).append(": ").append(headers.value(i))
                .append("\n")
            log(headers.name(i) + ": " + headers.value(i))
        }
        gioHttp.requestHeader = requestHeaderSb.toString()

        if (bodyHasUnknownEncoding(request.headers())) {
            gioHttp.requestBody = "encoded body omitted"
            log("--> END ${request.method()} (encoded body omitted)")
        } else if (bodyHasSnappyEncoding(request.headers())) {
            val buffer = Buffer()
            requestBody!!.writeTo(buffer)

            log("")
            val byteArray = buffer.readByteArray()
            val compressedOut = XORUtils.encrypt(byteArray, (gioHttp.httpTime and 0xFF).toInt())
            gioHttp.requestBody = String(Snappy.uncompress(compressedOut, 0, compressedOut.size))
            log(gioHttp.requestBody)
            log("--> END ${request.method()} (${requestBody.contentLength()}-byte body)")
        } else {
            val buffer = Buffer()
            requestBody!!.writeTo(buffer)

            val contentType = requestBody.contentType()
            val charset: Charset =
                contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

            log("")
            if (buffer.isProbablyUtf8()) {
                gioHttp.requestBody = buffer.readString(charset)
                log(gioHttp.requestBody)
                log("--> END ${request.method()} (${requestBody.contentLength()}-byte body)")
            } else {
                gioHttp.requestBody = buffer.readString(charset)
                //gioHttp.requestBody = "encoded body omitted (e.g. protobuf)"
                log("--> END ${request.method()} (binary ${requestBody.contentLength()}-byte body omitted. e.g. protobuf)")
            }
        }

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            log("<-- HTTP FAILED: $e")
            gioHttp.responseCode = 400
            gioHttp.responseMessage = "Error"
            GioKitDbManager.instance.insertHttp(gioHttp)
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        gioHttp.httpCost = tookMs

        val responseBody = response.body()!!
        val contentLength = responseBody.contentLength()
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
        gioHttp.responseCode = response.code()
        gioHttp.responseSize = contentLength
        gioHttp.responseUrl = response.request().url().toString()
        gioHttp.responseMessage = if (response.message().isEmpty()) "" else response.message()
        log(
            "<-- ${response.code()}${
                if (response.message().isEmpty()) "" else ' ' + response.message()
            } ${response.request().url()} (${tookMs}ms${",$bodySize body"})"
        )
        val responseHeaderSb = StringBuilder()
        val responseHeaders = response.headers()
        for (i in 0 until responseHeaders.size()) {
            log(responseHeaders.name(i) + ": " + responseHeaders.value(i))
            responseHeaderSb.append(responseHeaders.name(i)).append(": ")
                .append(responseHeaders.value(i)).append("\n")
            gioHttp.responseHeader = responseHeaderSb.toString()
        }

        if (bodyHasUnknownEncoding(response.headers())) {
            gioHttp.responseBody = "encoded body omitted"
            log("<-- END HTTP (encoded body omitted)")
        } else {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            var buffer = source.buffer()

            var gzippedLength: Long? = null
            if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                gzippedLength = buffer.size()
                GzipSource(buffer.clone()).use { gzippedResponseBody ->
                    buffer = Buffer()
                    buffer.writeAll(gzippedResponseBody)
                }
            }

            val contentType = responseBody.contentType()
            val charset: Charset =
                contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

            if (!buffer.isProbablyUtf8()) {
                log("")
                gioHttp.responseBody = "encoded body omitted"
                log("<-- END HTTP (binary ${buffer.size()}-byte body omitted)")
                return response
            }

            if (contentLength != 0L) {
                log("")
                gioHttp.responseBody = buffer.clone().readString(charset)
                log(buffer.clone().readString(charset))
            }

            if (gzippedLength != null) {
                log("<-- END HTTP (${buffer.size()}-byte, $gzippedLength-gzipped-byte body)")
            } else {
                log("<-- END HTTP (${buffer.size()}-byte body)")
            }
        }
        GioKitDbManager.instance.insertHttp(gioHttp)
        return response
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }

    private fun bodyHasSnappyEncoding(headers: Headers): Boolean {
        val contentCrypt = headers["X-Crypt-Codec"] ?: return false
        val contentCompress = headers["X-Compress-Codec"] ?: return false
        return contentCrypt.equals("1", ignoreCase = true) &&
                contentCompress.equals("2", ignoreCase = true)
    }

    fun log(message: String) {
        //Log.d("HttpCapture", message)
    }

    internal fun Buffer.isProbablyUtf8(): Boolean {
        try {
            val prefix = Buffer()
            val byteCount = size().coerceAtMost(64)
            copyTo(prefix, 0, byteCount)
            for (i in 0 until 16) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            return true
        } catch (_: EOFException) {
            return false // Truncated UTF-8 sequence.
        }
    }

}