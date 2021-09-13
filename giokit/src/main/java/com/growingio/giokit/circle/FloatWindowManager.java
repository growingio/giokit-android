package com.growingio.giokit.circle;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.growingio.giokit.GioKitImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;


/**
 * author CliffLeopard
 * time   2017/9/28:下午2:32
 * email  gaoguanling@growingio.com
 */
public class FloatWindowManager {

    private final String TAG = "GIO.FloatWindowManager";
    private static volatile FloatWindowManager floatWindowManager;
    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams params;


    public static FloatWindowManager getInstance() {
        init(GioKitImpl.APPLICATION);
        return floatWindowManager;
    }

    private static void init(Context context) {
        if (floatWindowManager == null) {
            synchronized (FloatWindowManager.class) {
                if (floatWindowManager == null)
                    floatWindowManager = new FloatWindowManager(context);
            }
        }
    }

    private FloatWindowManager(Context context) {
        this.mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }


    Display getDefaultDisplay() {
        return mWindowManager.getDefaultDisplay();
    }


    /**
     * 显示浮窗
     *
     * @param mView
     */
    public void addView(View mView, WindowManager.LayoutParams params) {
        params.type = getParamsType();
        if (mView.getParent() == null) {
            try {
                mWindowManager.addView(mView, params);
            } catch (Exception ignore) {
                Log.e(TAG, "WindowManager addView Failed:" + ignore.toString());
            }
        }
    }

    /**
     * 更新浮窗
     *
     * @param mView
     */
    public void updateViewLayout(View mView, WindowManager.LayoutParams params) {

        params.type = getParamsType();
        try {
            mWindowManager.updateViewLayout(mView, params);
        } catch (Exception ignore) {
            Log.e(TAG, "WindowManager updateViewLayout Failed");
        }

    }

    private int getParamsType() {

        int type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if (Build.VERSION.SDK_INT >= 26) {
                type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N || isMIUIV8()) {
                type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                type = WindowManager.LayoutParams.TYPE_TOAST;
            }
        } else {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        return type;
    }


    /**
     * 隐藏浮窗
     */
    public void removeView(View mView) {
        if (mView != null && mView.getParent() != null) {
            mWindowManager.removeView(mView);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean isMIUIV8() {
        String manufacturer = Build.MANUFACTURER;
        if (TextUtils.isEmpty(manufacturer)) {
            return false;
        }
        if (!manufacturer.toLowerCase(Locale.getDefault()).contains("xiaomi")) {
            return false;
        }
        try (FileInputStream inputStream = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"))) {
            Properties properties = new Properties();
            properties.load(inputStream);
            String versionName = properties.getProperty("ro.miui.ui.version.name");
            if (!TextUtils.isEmpty(versionName) && versionName.equalsIgnoreCase("V8")) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
