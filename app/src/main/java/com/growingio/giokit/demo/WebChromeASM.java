package com.growingio.giokit.demo;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.growingio.giokit.hook.GioWebView;

/**
 * <p>
 *
 * @author cpacm 2021/9/9
 */
public class WebChromeASM extends WebChromeClient {
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        //GioWebView.addCircleJsToWebView(view, newProgress);
    }
}
