package com.growingio.giokit.demo;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.ClientCertRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.growingio.giokit.circle.ViewHelper;
import com.growingio.giokit.hook.GioWebView;

/**
 * Created by liangdengke on 2018/11/27.
 */
public class WebCircleHybridActivity extends Activity {
    private final String TAG = "WebCircleHybridActivity";

    WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_circle);
        webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ViewHelper.callJavaScript(view, "_vds_hybrid.findElementAtPoint");
            }

            @Override
            public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                super.onReceivedClientCertRequest(view, request);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }


        });
        webView.loadUrl("http://cpacm.8bgm.com/");
        webView.addJavascriptInterface(new GioWebView.VdsBridge(), "_vds_bridge");
    }
}
