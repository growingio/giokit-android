package com.growingio.giokit.launch.sdkh5door

import android.os.Build
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ProgressBar
import com.growingio.giokit.GioKitImpl
import com.growingio.giokit.R
import com.growingio.giokit.hook.GioPluginConfig
import com.growingio.giokit.hook.GioWebView
import com.growingio.giokit.hook.GioWebView.WEBJS_HOOK_JAVASCRIPT
import com.growingio.giokit.launch.BaseFragment
import com.growingio.giokit.utils.SdkV3InfoUtils
import java.lang.ref.WeakReference

/**
 * <p>
 *
 * @author cpacm 2023/2/1
 */
class SdkH5Fragment : BaseFragment(), SdkH5WebView.OnWebViewChangedListener {
    override fun layoutId(): Int {
        return R.layout.fragment_giokit_h5
    }

    private lateinit var h5Web: SdkH5WebView
    private lateinit var progressBar: ProgressBar
    private var hybridWebView = false
    private var jsKitWebView = false

    override fun onViewCreated(view: View?) {
        h5Web = findViewById(R.id.h5Web)
        h5Web.addJavascriptInterface(GioWebView.VdsBridge(), "_gio_bridge")
        progressBar = findViewById(R.id.progressBar)
        val h5VideoView = findViewById<ViewGroup>(R.id.h5VideoView)
        h5Web.setOnWebViewChangedListener(this)
        h5Web.setFullScreenView(h5VideoView)

        val url = arguments?.getString("url") ?: "about:blank"
        hybridWebView = arguments?.getBoolean("hybrid_enabled") ?: false
        jsKitWebView = arguments?.getBoolean("giokit_enabled") ?: false
        h5Web.loadUrl(url)

        if (hybridWebView) {
            if (!GioPluginConfig.isSaasSdk) {
                SdkV3InfoUtils.hookWebView(h5Web)
            }
        }
    }

    override fun onDestroyView() {
        h5Web.removeWebViewChangedListener()
        super.onDestroyView()
    }


    override fun onGetTitle(): String {
        return getString(R.string.giokit_menu_h5door)
    }

    override fun onTitleChanged(title: String?) {
        setTitle(title)
    }

    override fun onBackPressed(): Boolean {
        if (h5Web.isFullVideoScreen()) {
            h5Web.onBackPressed()
            return true
        }
        if (h5Web.canGoBack()) {
            h5Web.goBack()
            return true
        }
        return false
    }


    override fun onLoadingProgress(progress: Int) {
        hookJsToWebView(h5Web, progress)

        progressBar.visibility = View.VISIBLE
        if (progress >= 90) {
            progressBar.visibility = View.GONE
        }
        progressBar.progress = progress
    }

    private fun hookJsToWebView(webView: WebView, progress: Int) {
        val oldView = GioKitImpl.webView.get()
        if (oldView == null || oldView != webView) {
            webView.addJavascriptInterface(GioWebView.VdsBridge(), "_gio_bridge")
            GioKitImpl.webView = WeakReference(webView)
        }
        if (progress >= GioWebView.MIN_PROGRESS_FOR_HOOK) {
            webView.handler?.removeMessages(0)
            val message = Message.obtain(webView.handler) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (jsKitWebView) {
                        webView.evaluateJavascript(WEBJS_HOOK_JAVASCRIPT, null)
                    }
                    webView.evaluateJavascript(
                        GioWebView.getAssets(webView.context, "gio_hybrid.min.js"),
                        null
                    )
                } else {
                    if (jsKitWebView) {
                        webView.loadUrl(WEBJS_HOOK_JAVASCRIPT)
                    }
                    webView.loadUrl(GioWebView.getAssets(webView.context, "gio_hybrid.min.js"))
                }
            }
            message.what = 0
            webView.handler?.sendMessageDelayed(message, GioWebView.HOOK_CIRCLE_DELAY)
        }
    }
}