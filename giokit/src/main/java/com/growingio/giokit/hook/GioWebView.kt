package com.growingio.giokit.hook

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Message
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.growingio.android.sdk.autotrack.AutotrackConfig
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.android.sdk.autotrack.view.ViewNode
import com.growingio.giokit.GioKitImpl
import com.growingio.giokit.utils.limitLength
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset

/**
 * <p>
 *
 * @author cpacm 2021/9/8
 */
object GioWebView {
    private const val WEBVIEW_JS_TAG = 100 shl 24
    private const val MIN_PROGRESS_FOR_HOOK = 60
    private const val HOOK_CIRCLE_DELAY = 500L

    private var currentUrl: String? = null

    /*************************** Webview inject ****************************/
    @SuppressLint("SetJavaScriptEnabled")
    @JvmStatic
    fun addCircleJsToWebView(webView: WebView, progress: Int) {
        val oldView = GioKitImpl.webView.get()
        if (oldView == null || oldView != webView) {
            webView.addJavascriptInterface(GioWebView.VdsBridge(), "GiokitTouchJavascriptBridge")
            GioKitImpl.webView = WeakReference(webView)
        }
        if (progress >= MIN_PROGRESS_FOR_HOOK) {
            webView.handler?.removeMessages(0)
            val message = Message.obtain(webView.handler) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript(
                        GioWebView.getAssets(webView.context, "giokit_touch.js"),
                        null
                    )
                } else {
                    webView.loadUrl(GioWebView.getAssets(webView.context, "giokit_touch.js"))
                }
            }
            message.what = 0
            webView.handler?.sendMessageDelayed(message, HOOK_CIRCLE_DELAY)
            currentUrl = getWebUrl()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @JvmStatic
    fun addCircleJsToX5(webView: com.tencent.smtt.sdk.WebView, progress: Int) {
        val oldView = GioKitImpl.webView.get()
        if (oldView == null || oldView != webView) {
            webView.addJavascriptInterface(VdsBridge(), "GiokitTouchJavascriptBridge")
            GioKitImpl.webView = WeakReference(webView)
        }
        if (progress >= MIN_PROGRESS_FOR_HOOK) {
            webView.handler?.removeMessages(0)
            val message = Message.obtain(webView.handler) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript(
                        getAssets(webView.context, "giokit_touch.js"),
                        null
                    )
                } else {
                    webView.loadUrl(getAssets(webView.context, "giokit_touch.js"))
                }
            }
            message.what = 0
            webView.handler?.sendMessageDelayed(message, HOOK_CIRCLE_DELAY)
            currentUrl = getWebUrl()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @JvmStatic
    fun addCircleJsToUc(webView: com.uc.webview.export.WebView, progress: Int) {
        val oldView = GioKitImpl.webView.get()
        if (oldView == null || oldView != webView) {
            webView.addJavascriptInterface(VdsBridge(), "GiokitTouchJavascriptBridge")
            GioKitImpl.webView = WeakReference(webView)
        }
        if (progress >= MIN_PROGRESS_FOR_HOOK) {
            webView.handler?.removeMessages(0)
            val message = Message.obtain(webView.handler) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript(
                        getAssets(webView.context, "giokit_touch.js"),
                        null
                    )
                } else {
                    webView.loadUrl(getAssets(webView.context, "giokit_touch.js"))
                }
            }
            message.what = 0
            webView.handler?.sendMessageDelayed(message, HOOK_CIRCLE_DELAY)
            currentUrl = getWebUrl()
        }
    }

    fun getWebUrl(): String? {
        val webView = GioKitImpl.webView.get() ?: return null
        if (webView is WebView) return webView.url
        if (webView is com.tencent.smtt.sdk.WebView) return webView.url
        if (webView is com.uc.webview.export.WebView) return webView.url
        return null
    }


    /*************************** e n d ****************************/

    /**
     * 读取Assets目录下的文件
     *
     * @param context
     * @param name
     * @return
     */
    fun getAssets(context: Context, name: String): String {
        var result: String? = null
        try {
            val `in` = context.assets.open(name)  //获得AssetManger 对象, 调用其open 方法取得  对应的inputStream对象
            val size = `in`.available()//取得数据流的数据大小
            val buffer = ByteArray(size)
            `in`.read(buffer)
            `in`.close()
            result = String(buffer)
        } catch (e: Exception) {
        }
        return result ?: ""
    }

    open class VdsBridge {
        @com.uc.webview.export.JavascriptInterface
        @JavascriptInterface
        open fun hoverNodes(message: String) {
            Log.d("hoverNodes", message ?: "")
            try {
                val nodeJson = JSONObject(message)
                val node: ViewNode = object : ViewNode {
                    override fun getView(): View? {
                        return GioKitImpl.webView.get()
                    }

                    override fun getXPath(): String {
                        val xpath = nodeJson.optString("skeleton")
                        if (xpath.isEmpty()) {
                            return nodeJson.optString("xpath")
                        }
                        return xpath
                    }

                    override fun getViewContent(): String {
                        return nodeJson.optString("content")
                    }

                    override fun getIndex(): Int {
                        return nodeJson.optInt("index", -1)
                    }

                    override fun getXIndex(): String? {
                        val xindex = nodeJson.optString("xindex")
                        return if (xindex.isEmpty()) null else xindex
                    }
                }
                GioKitImpl.gioKitHoverManager.anchorView?.setCircleInfo(node, URLDecoder.decode(currentUrl, "UTF-8").limitLength(50))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}