package com.growingio.giokit.v3;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.growingio.android.sdk.TrackerContext;
import com.growingio.android.sdk.autotrack.GrowingAutotracker;

import java.util.HashMap;

/**
 * <p>
 *
 * @author cpacm 2021/8/23
 */
public class LambdaTest {


    public void test(Button button) {
        button.setOnClickListener(v -> {
            GrowingAutotracker.get().trackCustomEvent("lambda");
        });
    }

    public void doubleText() {
        GrowingAutotracker.get().trackCustomEvent("double1");
        GrowingAutotracker.get().trackCustomEvent("double2");
    }

    public void third() {
        GrowingAutotracker.get().trackCustomEvent("double1");
        GrowingAutotracker.get().trackCustomEvent("double2");
        GrowingAutotracker.get().trackCustomEvent("double3");
    }

    public void webText(Context context) {
        WebView webView = new WebView(context);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://www.baidu.com");
    }

}
