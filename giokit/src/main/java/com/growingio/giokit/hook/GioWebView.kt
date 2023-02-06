package com.growingio.giokit.hook

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Message
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.preference.PreferenceManager
import com.growingio.giokit.GioKitImpl
import com.growingio.giokit.circle.ViewNode
import com.growingio.giokit.circle.ViewNode.getWebNodesFromEvent
import org.json.JSONObject
import java.lang.ref.WeakReference

/**
 * <p>
 *
 * @author cpacm 2021/9/8
 */
object GioWebView {
    const val WEBVIEW_JS_TAG = 100 shl 24
    const val MIN_PROGRESS_FOR_HOOK = 60
    const val HOOK_CIRCLE_DELAY = 500L

    private const val WEBJS_GIOKIT_PATH = "https://assets.giocdn.com/sdk/webjs/giokit.min.js"
    const val WEBJS_HOOK_JAVASCRIPT =
        "javascript:import('$WEBJS_GIOKIT_PATH').then(() => { if(!window.gioKit){window.gioKit=new GioKit();}})"


    /*************************** Webview inject ****************************/
    @JvmStatic
    fun addWebJsGiokit(forceAdd: Boolean = false, hookWebView: (javascript: String) -> Unit) {
        val sp = PreferenceManager.getDefaultSharedPreferences(GioKitImpl.APPLICATION)
        val enabled = sp.getBoolean("GIOKIT_WEBVIEW_JS_ENABLED", false)
        if (!enabled && !forceAdd) return
        try {
            //hookWebView(getAssets(GioKitImpl.APPLICATION,"giokit.min.js"))
            hookWebView(WEBJS_HOOK_JAVASCRIPT)
        } catch (e: Exception) {
            Log.e("GIOKIT", "WebJs Giokit Hook Failed")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @JvmStatic
    fun addCircleJsToWebView(webView: WebView, progress: Int) {
        val oldView = GioKitImpl.webView.get()
        if (oldView == null || oldView != webView) {
            webView.addJavascriptInterface(VdsBridge(), "_gio_bridge")
            GioKitImpl.webView = WeakReference(webView)
        }
        if (progress >= MIN_PROGRESS_FOR_HOOK) {
            webView.handler?.removeMessages(0)
            val message = Message.obtain(webView.handler) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript(
                        getAssets(webView.context, "gio_hybrid.min.js"),
                        null
                    )

                } else {
                    webView.loadUrl(getAssets(webView.context, "gio_hybrid.min.js"))
                }

            }
            message.what = 0
            webView.handler?.sendMessageDelayed(message, HOOK_CIRCLE_DELAY)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @JvmStatic
    fun addCircleJsToX5(webView: com.tencent.smtt.sdk.WebView, progress: Int) {
        val oldView = GioKitImpl.webView.get()
        if (oldView == null || oldView != webView) {
            webView.addJavascriptInterface(VdsBridge(), "_gio_bridge")
            GioKitImpl.webView = WeakReference(webView)
        }
        if (progress >= MIN_PROGRESS_FOR_HOOK) {
            webView.handler?.removeMessages(0)
            val message = Message.obtain(webView.handler) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript(
                        getAssets(webView.context, "gio_hybrid.min.js"),
                        null
                    )
                } else {
                    webView.loadUrl(getAssets(webView.context, "gio_hybrid.min.js"))
                }
            }
            message.what = 0
            webView.handler?.sendMessageDelayed(message, HOOK_CIRCLE_DELAY)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @JvmStatic
    fun addCircleJsToUc(webView: com.uc.webview.export.WebView, progress: Int) {
        val oldView = GioKitImpl.webView.get()
        if (oldView == null || oldView != webView) {
            webView.addJavascriptInterface(VdsBridge(), "_gio_bridge")
            GioKitImpl.webView = WeakReference(webView)
        }
        if (progress >= MIN_PROGRESS_FOR_HOOK) {
            webView.handler?.removeMessages(0)
            val message = Message.obtain(webView.handler) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript(
                        getAssets(webView.context, "gio_hybrid.min.js"),
                        null
                    )
                } else {
                    webView.loadUrl(getAssets(webView.context, "gio_hybrid.min.js"))
                }
            }
            message.what = 0
            webView.handler?.sendMessageDelayed(message, HOOK_CIRCLE_DELAY)
        }
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
        @JavascriptInterface
        open fun hoverNodes(message: String?) {
            Log.d("hoverNodes", message ?: "")
            try {
                val `object` = JSONObject(message ?: "{}")
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