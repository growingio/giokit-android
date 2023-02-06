package com.growingio.giokit.launch.sdkh5door

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar

@SuppressLint("SetJavaScriptEnabled")
class SdkH5WebView : WebView {

    private var fullScreenView: ViewGroup? = null
    private val videoWebChromeClient by lazy { VideoWebChromeClient() }
    private var onWebViewChangedListener: OnWebViewChangedListener? = null
    private var isVideoFullscreen: Boolean = false
    private var toolbar: Toolbar? = null

    constructor(context: Context) : super(
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1)
            context.createConfigurationContext(Configuration())
        else context
    )

    constructor(context: Context, attrs: AttributeSet) : super(
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1)
            context.createConfigurationContext(Configuration())
        else context, attrs
    )

    init {
        val webSettings = settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true//自适应屏幕
        webSettings.mediaPlaybackRequiresUserGesture = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }
        webSettings.allowFileAccess = true
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.domStorageEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.databaseEnabled = true
        webSettings.loadsImagesAutomatically = true
        if (Build.VERSION.SDK_INT >= 21)
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        this.setDownloadListener { url, _, _, _, _ ->
            try {
                val uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        webChromeClient = videoWebChromeClient
        webViewClient = BaseWebClient()
    }

    fun setFullScreenView(fullScreenView: ViewGroup) {
        this.fullScreenView = fullScreenView
    }

    private inner class VideoWebChromeClient : WebChromeClient() {

        private var videoViewContainer: FrameLayout? = null
        private var videoViewCallback: CustomViewCallback? = null

        override fun onReceivedTitle(view: WebView?, title: String?) {
            onWebViewChangedListener?.onTitleChanged(title)
            super.onReceivedTitle(view, title)
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            onWebViewChangedListener?.onLoadingProgress(newProgress)
            super.onProgressChanged(view, newProgress)
        }

        /**
         * Available in API level 14+, deprecated in API level 18+
         */
        @Deprecated("Deprecated in Java")
        override fun onShowCustomView(
            view: View,
            requestedOrientation: Int,
            callback: CustomViewCallback
        ) {
            onShowCustomView(view, callback)
        }

        override fun onShowCustomView(view: View, callback: CustomViewCallback) {
            if (view is FrameLayout && fullScreenView != null) {
                // A video wants to be shown
                this.videoViewContainer = view
                this.videoViewCallback = callback
                fullScreenView!!.addView(
                    videoViewContainer,
                    LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                )
                fullScreenView!!.visibility = View.VISIBLE
                isVideoFullscreen = true
                fullScreenView?.keepScreenOn = true
                if (context is Activity) {
                    val activity = context as Activity
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                    toolbar?.visibility = View.GONE
                    activity.window.setFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN
                    )
                }
            }
        }

        @SuppressLint("SourceLockedOrientationActivity")
        override fun onHideCustomView() {
            if (isVideoFullscreen && fullScreenView != null) {
                // Hide the video view, remove it, and show the non-video view
                fullScreenView!!.visibility = View.INVISIBLE
                fullScreenView!!.removeView(videoViewContainer)

                // Call back (only in API level <19, because in API level 19+ with chromium webview it crashes)
                if (videoViewCallback != null && !videoViewCallback!!.javaClass.name.contains(".chromium.")) {
                    videoViewCallback!!.onCustomViewHidden()
                }

                if (context is Activity) {
                    val activity = context as Activity
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    toolbar?.visibility = View.VISIBLE
                    activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                }
                isVideoFullscreen = false
                fullScreenView?.keepScreenOn = false
                videoViewContainer = null
                videoViewCallback = null
            }
        }

        fun onBackPressed(): Boolean {
            if (isVideoFullscreen) {
                onHideCustomView()
                return true
            } else {
                return false
            }
        }

        override fun getDefaultVideoPoster(): Bitmap? {
            return super.getDefaultVideoPoster()
        }
    }

    private inner class BaseWebClient : WebViewClient() {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }

        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            return super.shouldOverrideUrlLoading(view, url)
        }

        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
            if (!settings.loadsImagesAutomatically) {
                settings.loadsImagesAutomatically = true
            }
            onWebViewChangedListener?.onLoadingProgress(0)
            onPageStartCallback(view, url)
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            onWebViewChangedListener?.onLoadingProgress(100)
            onPageFinishedCallback(view, url)
        }

        override fun onLoadResource(view: WebView?, url: String?) {
            onWebViewChangedListener?.onResourceLoad(url)
            super.onLoadResource(view, url)
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
            try {
                if (request?.url?.toString()?.contains("giokit.min.js") == true) {
                    Log.d("GIOKIT WEBVIEW", "load local giokit.min.js")
                    val headers = hashMapOf<String,String>()
                    headers.put("Access-Control-Allow-Origin","*")
                    headers.put("Access-Control-Allow-Headers","X-Requested-With")
                    headers.put("Access-Control-Allow-Methods","POST,GET,OPTIONS")
                    headers.put("Access-Control-Allow-Credentials","true")
                    return WebResourceResponse(
                        "application/x-javascript",
                        "utf-8",200,"ok",headers,
                        context.assets.open("giokit.min.js")
                    )
                }
            } catch (e: Exception) {
                Log.e("GIOKIT WEBVIEW", e.toString())
            }
            return super.shouldInterceptRequest(view, request)
        }

    }

    open fun onPageStartCallback(view: WebView, url: String?) {

    }

    open fun onPageFinishedCallback(view: WebView?, url: String?) {
    }

    fun onBackPressed(): Boolean {
        return videoWebChromeClient.onBackPressed()
    }

    fun setOnWebViewChangedListener(onWebViewChangedListener: OnWebViewChangedListener) {
        this.onWebViewChangedListener = onWebViewChangedListener
    }

    fun removeWebViewChangedListener() {
        this.onWebViewChangedListener = null
    }

    fun isFullVideoScreen(): Boolean {
        return isVideoFullscreen
    }

    /**
     * Impliment in the activity/fragment/view that you want to listen to the webview
     */
    interface OnWebViewChangedListener {
        fun onTitleChanged(title: String?)

        fun onLoadingProgress(progress: Int)

        fun onResourceLoad(url: String?) {}
    }
}
