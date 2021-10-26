package com.growingio.giokit.saas;

import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * <p>
 *
 * @author cpacm 2021/10/13
 */
public class MoreChromeClient{

    public void initWebview(Context context){
        WebView webView = new WebView(context);
        webView.setWebViewClient(new WebViewClient(){

        });
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("https://www.baidu.com");
    }

}
