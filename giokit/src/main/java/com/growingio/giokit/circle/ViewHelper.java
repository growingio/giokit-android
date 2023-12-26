package com.growingio.giokit.circle;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.growingio.giokit.GioKitImpl;

import io.mattcarroll.hover.HoverView;

/**
 * Created by xyz on 15/11/3.
 */
public class ViewHelper {
    private final static String TAG = "GIO.ViewHelper";

    public static boolean isWindowNeedTraverse(View root, String prefix, boolean skipOtherActivity) {
        if (GioKitImpl.curActivity.get() != null && root.hashCode() == GioKitImpl.curActivity.get().getWindow().getDecorView().hashCode())
            return true;
        return !(root instanceof FloatViewContainer
                || !(root instanceof ViewGroup)
                || root instanceof HoverView
                || (skipOtherActivity
                && (root.getWindowVisibility() == View.GONE
                || root.getVisibility() != View.VISIBLE
                || TextUtils.equals(prefix, WindowHelper.getMainWindowPrefix())
                || root.getWidth() == 0
                || root.getHeight() == 0))
        );
    }

    public static int getMainWindowCount(View[] windowRootViews) {
        int mainWindowCount = 0;
        WindowHelper.init();
        for (View windowRootView : windowRootViews) {
            if (windowRootView == null) continue;
            String prefix = WindowHelper.getWindowPrefix(windowRootView);
            mainWindowCount += prefix.equals(WindowHelper.getMainWindowPrefix()) ? 1 : 0;
        }
        return mainWindowCount;
    }


    //https://assets.giocdn.com/sdk/hybrid/1.1/vds_hybrid_circle_plugin.min.js?sdkVer=autotrack-2.9.2-SNAPSHOT_e08f56f7&platform=Android
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void callJavaScript(View view, String methodName, Object... params) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("try{(function(){");
        stringBuilder.append(methodName);
        stringBuilder.append("(");
        String separator = "";
        for (Object param : params) {
            stringBuilder.append(separator);
            separator = ",";
            if (param instanceof String) {
                stringBuilder.append("'");
                param = ((String) param).replace("'", "\'");
                StringBuilder builder = new StringBuilder();
                LinkedString.stringWithoutQuotation(builder, (String) param);
                param = builder.toString();
            }
            stringBuilder.append(param);
            if (param instanceof String) {
                stringBuilder.append("'");
            }
        }
        stringBuilder.append(");})()}catch(ex){console.log(ex);}");
        try {
            String jsCode = stringBuilder.toString();
            if (view instanceof WebView) {
                WebView webView = (WebView) view;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript(jsCode, null);
                } else {
                    webView.loadUrl("javascript:" + jsCode);
                }
            }
        } catch (Exception e) {
        }
    }
}
