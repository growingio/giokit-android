package com.growingio.giokit.hook

import android.annotation.SuppressLint
import android.content.Context
import android.os.Message
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.growingio.giokit.GioKitImpl
import com.growingio.giokit.circle.ViewNode
import com.growingio.giokit.circle.ViewNode.getWebNodesFromEvent
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

/**
 * <p>
 *
 * @author cpacm 2021/9/8
 */
object GioWebView {
    private const val WEBVIEW_JS_TAG = 100 shl 24

    private const val JS_HYBRID_LOCAL_URL = "file:///android_asset/app_hybrid.js"
    const val JS_HYBRID_URL =
        "https://assets.giocdn.com/sdk/hybrid/2.0/gio_hybrid.min.js?sdkVer=autotrack-2.9.2-SNAPSHOT_e08f56f7&platform=Android"
    private const val JS_CIRCLE_LOCAL_URL = "file:///android_asset/app_circle_plugin.js"
    private const val CIRCLE_JS_ADDRESS =
        "https://assets.giocdn.com/sdk/hybrid/1.1/vds_hybrid_circle_plugin.min.js?sdkVer=autotrack-2.9.2-SNAPSHOT_e08f56f7&platform=Android"
    private const val MIN_PROGRESS_FOR_HOOK = 60
    private const val HOOK_CIRCLE_DELAY = 500L

    @SuppressLint("SetJavaScriptEnabled")
    @JvmStatic
    fun addCircleJsToWebView(webView: WebView, progress: Int) {
        val oldView = GioKitImpl.webView.get()
        if (oldView == null || oldView != webView) {
            webView.addJavascriptInterface(VdsBridge(), "_vds_bridge")
            GioKitImpl.webView = WeakReference(webView)
        }
        if (progress >= MIN_PROGRESS_FOR_HOOK) {
            webView.handler?.removeMessages(0)
            webView.loadUrl(getInitPatternServer())
            webView.loadUrl(getVdsHybridConfig())
            //webView.evaluateJavascript(injectScriptFile("_gio_hybrid_js", JS_HYBRID_URL),null)
            val message = Message.obtain(webView.handler) {
                webView.evaluateJavascript(getAssets(webView.context, "app_hybrid.js"), null)
                webView.evaluateJavascript(getAssets(webView.context, "app_circle_plugin.js"), null)
                //webView.loadUrl(injectScriptFile("_gio_hybrid_js", JS_HYBRID_LOCAL_URL))
                //webView.loadUrl(injectScriptFile("_gio_circle_js", JS_CIRCLE_LOCAL_URL))
            }
            message.what = 0
            webView.handler?.sendMessageDelayed(message, HOOK_CIRCLE_DELAY)
        }
    }

    private fun getVdsHybridConfig(): String {
        val hybridJSConfig = java.lang.String.format(
            "window._vds_hybrid_config = {\"enableHT\":%s,\"disableImp\":%s, \"protocolVersion\":1}",
            true,
            false
        )
        return String.format("javascript:(function(){try{%s}catch(e){}})()", hybridJSConfig)
    }

    private fun getInitPatternServer(): String {
        try {
            val jsonObject = JSONObject()
            jsonObject.put("ai", "ai")
            jsonObject.put("cs1", "cs1")
            jsonObject.put("d", "d")
            jsonObject.put("p", System.currentTimeMillis().toString())
            jsonObject.put("gtaHost", "gtaHost")
            jsonObject.put("x", "xpath")
            jsonObject.put("s", "session")
            jsonObject.put("token", "token")
            jsonObject.put("u", "deviceId")
            val patternServerJs =
                "window._vds_hybrid_native_info = " + jsonObject.toString() + ";"
            return String.format("javascript:(function(){try{%s}catch(e){}})()", patternServerJs)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return ""

    }

    private fun injectScriptFile(id: String, scriptSrc: String): String {
        val js = """javascript:(function(){try{var jsNode = document.getElementById('%s');
                    if (jsNode==null) {
                        var p = document.createElement('script');
                        p.src = '%s';
                        p.id = '%s';
                        document.head.appendChild(p);
                    }}catch(e){}})()"""
        return String.format(js, id, scriptSrc, id)
    }

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
        @JavascriptInterface
        open fun webCircleHybridEvent(event: String?) {
            Log.d("hybrid", "webCircleHybridEvent")
        }

        @JavascriptInterface
        open fun saveEvent(event: String?) {
            Log.d("hybrid", "saveEvent")
        }

        @JavascriptInterface
        open fun setVisitor(event: String?) {
            Log.d("hybrid", "setVisitor")
        }

        @JavascriptInterface
        open fun saveCustomEvent(event: String?) {
            Log.d("hybrid", "saveCustomEvent")
        }

        @JavascriptInterface
        open fun onDOMChanged() {
            Log.d("hybrid", "onDOMChanged")
        }

        @JavascriptInterface
        open fun hoverNodes(message: String?) {
            Log.d("hoverNodes", message ?: "")
            try {
                val `object` = JSONObject(message)
                val type = `object`.getString("t")
                if (type == "snap") {
                    val nodes: List<ViewNode> = getWebNodesFromEvent(`object`)
                    GioKitImpl.gioKitHoverManager.anchorView?.setCircleInfo(nodes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}