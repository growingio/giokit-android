package com.growingio.saas;

import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * <p>
 *
 * @author cpacm 2021/10/13
 */
public class CustomChromeClient extends WebChromeClient {

    @Override
    public void onCloseWindow(WebView window) {
        Log.d("test", "onCloseWindow");
    }

    @Override
    public void onProgressChanged(WebView var1, int var2) {
        Log.d("test", "onProgressChanged");
        super.onProgressChanged(var1, var2);
    }
}
