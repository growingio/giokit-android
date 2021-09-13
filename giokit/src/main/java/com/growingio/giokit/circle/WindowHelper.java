package com.growingio.giokit.circle;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.view.menu.ListMenuItemView;


import com.growingio.giokit.GioKitImpl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.WeakHashMap;

/**
 * Created by xyz on 15/11/12.
 */
public class WindowHelper {

    static Object sWindowManger;
    static Field viewsField;
    static Class sPhoneWindowClazz;
    static Class sPopupWindowClazz;
    private static final String sMainWindowPrefix = "/MainWindow";
    private static final String sDialogWindowPrefix = "/DialogWindow";
    private static final String sPopupWindowPrefix = "/PopupWindow";
    private static final String sCustomWindowPrefix = "/CustomWindow";
    public static final String sIgnoredWindowPrefix = "/Ignored";
    private static Class<?> sListMenuItemViewClazz;
    private static Method sItemViewGetDataMethod;
    private static boolean sIsInitialized = false;
    static boolean sArrayListWindowViews = false;
    static boolean sViewArrayWindowViews = false;
    static WeakHashMap<View, Long> showingToast = new WeakHashMap<>();


    // Only used for RootView
    private static Comparator<View> sViewSizeComparator = new Comparator<View>() {
        @Override
        public int compare(View lhs, View rhs) {
            return rhs.getWidth() * rhs.getHeight() - lhs.getWidth() * lhs.getHeight();
        }
    };

    public static void init() {
        if (sIsInitialized) return;
        String windowManagerClassName;
        if (Build.VERSION.SDK_INT >= 17) {
            windowManagerClassName = "android.view.WindowManagerGlobal";
        } else {
            windowManagerClassName = "android.view.WindowManagerImpl";
        }

        Class<?> windowManager = null;
        try {
            windowManager = Class.forName(windowManagerClassName);

            String windowManagerString;
            if (Build.VERSION.SDK_INT >= 17) {
                windowManagerString = "sDefaultWindowManager";
            } else if (Build.VERSION.SDK_INT >= 13) {
                windowManagerString = "sWindowManager";
            } else {
                windowManagerString = "mWindowManager";
            }

            viewsField = windowManager.getDeclaredField("mViews");

            Field instanceField = windowManager.getDeclaredField(windowManagerString);
            viewsField.setAccessible(true);
            if (viewsField.getType() == ArrayList.class) {
                sArrayListWindowViews = true;
            } else if (viewsField.getType() == View[].class) {
                sViewArrayWindowViews = true;
            }
            instanceField.setAccessible(true);
            sWindowManger = instanceField.get(null);

        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        }

        try {
            sListMenuItemViewClazz = Class.forName("com.android.internal.view.menu.ListMenuItemView");
            Class itemViewInterface = Class.forName("com.android.internal.view.menu.MenuView$ItemView");
            sItemViewGetDataMethod = itemViewInterface.getDeclaredMethod("getItemData");
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
        }

        try {
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                    sPhoneWindowClazz = Class.forName("com.android.internal.policy.PhoneWindow$DecorView");
                } catch (ClassNotFoundException exception) {
                    // for Android N
                    sPhoneWindowClazz = Class.forName("com.android.internal.policy.DecorView");
                }
            } else {
                sPhoneWindowClazz = Class.forName("com.android.internal.policy.impl.PhoneWindow$DecorView");
            }
        } catch (ClassNotFoundException e) {
        }
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                sPopupWindowClazz = Class.forName("android.widget.PopupWindow$PopupDecorView");
            } else {
                sPopupWindowClazz = Class.forName("android.widget.PopupWindow$PopupViewContainer");
            }
        } catch (ClassNotFoundException e) {
        }
        sIsInitialized = true;
    }

    public static String getWindowPrefix(View root) {
        String windowPrefix;
        if (GioKitImpl.curActivity.get() != null) {
            long hashCode = GioKitImpl.curActivity.get().getWindow().getDecorView().hashCode();
            if (root.hashCode() == hashCode) {
                windowPrefix = WindowHelper.getMainWindowPrefix();
                return windowPrefix;
            }
        }
        windowPrefix = WindowHelper.getSubWindowPrefix(root);
        return windowPrefix;
    }

    public static View[] getWindowViews() {
        View[] result = new View[0];
        if (sWindowManger == null) {
            // 如果无法获取WindowManager就只遍历当前Activity的内容
            if (GioKitImpl.curActivity.get() != null) {
                Activity current = GioKitImpl.curActivity.get();
                return new View[]{current.getWindow().getDecorView()};
            }
            return result;
        }
        try {
            View[] views = null;
            if (sArrayListWindowViews) {
                views = ((ArrayList<View>) viewsField.get(sWindowManger)).toArray(result);
            } else if (sViewArrayWindowViews) {
                views = (View[]) viewsField.get(sWindowManger);
            }
            if (views != null) {
                // views可能会得到空值,可以考虑不从WindowManager反射获取所有Window
                result = views;
            }
        } catch (Exception e) {
        }
        return filterNullAndDismissToastView(result);
    }

    public static View[] filterNullAndDismissToastView(View[] array) {
        List<View> list = new ArrayList<>(array.length);
        long currentTime = System.currentTimeMillis();
        for (View view : array) {
            if (view == null)
                continue;
            if (!showingToast.isEmpty()) {
                Long deadline = showingToast.get(view);
                if (deadline != null && currentTime > deadline) {
                    continue;
                }
            }
            list.add(view);
        }
        View[] result = new View[list.size()];
        list.toArray(result);
        return result;
    }

    public static void onToastShow(Toast toast) {
        try {
            View nextView = toast.getView();
            int duration = toast.getDuration();
            if (nextView == null)
                return;
            // 不需要知道精确时间, 大致给个LONG_DURATION + 10000 = 8000
            duration = Math.max(8000, duration);
            showingToast.put(nextView, duration + System.currentTimeMillis());
        } catch (Exception e) {
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static View[] getSortedWindowViews() {
        View[] views = getWindowViews();
        if (views.length > 1) {
            views = Arrays.copyOf(views, views.length);
            Arrays.sort(views, sViewSizeComparator);
        }
        return views;
    }


    public static String getMainWindowPrefix() {
        return sMainWindowPrefix;
    }

    public static String getSubWindowPrefix(View root) {
        ViewGroup.LayoutParams params = root.getLayoutParams();
        if (params != null && params instanceof WindowManager.LayoutParams) {
            WindowManager.LayoutParams windowParams = (WindowManager.LayoutParams) params;
            int type = windowParams.type;
            if (type == WindowManager.LayoutParams.TYPE_BASE_APPLICATION) {
                return sMainWindowPrefix;
            } else if (type < WindowManager.LayoutParams.LAST_APPLICATION_WINDOW && root.getClass() == sPhoneWindowClazz) {
                return sDialogWindowPrefix;
            } else if (type < WindowManager.LayoutParams.LAST_SUB_WINDOW && root.getClass() == sPopupWindowClazz) {
                return sPopupWindowPrefix;
            } else if (type < WindowManager.LayoutParams.LAST_SYSTEM_WINDOW) {
                return sCustomWindowPrefix;
            }
        }
        // if get WindowManager.LayoutParams failed, use Class type as fallback.
        Class rootClazz = root.getClass();
        if (rootClazz == sPhoneWindowClazz) {
            return sDialogWindowPrefix;
        } else if (rootClazz == sPopupWindowClazz) {
            return sPopupWindowPrefix;
        } else {
            return sCustomWindowPrefix;
        }
    }

    public static boolean isDecorView(View rootView) {
        if (!sIsInitialized) {
            init();
        }
        Class rootClass = rootView.getClass();
        return rootClass == sPhoneWindowClazz || rootClass == sPopupWindowClazz;
    }
}
