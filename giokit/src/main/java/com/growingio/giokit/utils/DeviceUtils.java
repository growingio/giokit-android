package com.growingio.giokit.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.RequiresPermission;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.regex.Pattern;

import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;
import static android.content.Context.WIFI_SERVICE;

/**
 * Created by zhangweida on 2018/6/26.
 */

public class DeviceUtils {
    private static final String TAG = "DeviceUtils";
    private static final String PREF_DEVICE = "com.kuaidi.daijia.driver.device_pref";
    private static final String KEY_UUID = "key_uuid";
    private static final String BRAND_HUAWEI = "huawei";
    private static final String BRAND_HONOR = "honor";
    private static final String BRAND_SAMSUNG = "samsung";
    private static final String BRAND_XIAOMI = "xiaomi";
    private static final String BRAND_HONGMI = "hongmi";

    private static String IMEI = "";
    private static String IMSI = "";
    private static String PHONE_NUMBER = null;
    private static Boolean ROOTED = null;

    /**
     * 获取CPU个数
     */
    public static int getCoreNum() {
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                // Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            // Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            // Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            Log.e(TAG, "getCoreNum", e);
            // Default to return 1 core
            return 1;
        }
    }

    /**
     * @param context
     * @return 手机总内存(兆)
     */
    public static long getTotalMemory(Context context) {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            if (!TextUtils.isEmpty(str2)) {
                arrayOfString = str2.split("\\s+");
                initial_memory = Integer.valueOf(arrayOfString[1]).intValue() / 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            }
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return initial_memory;// Byte转换为KB或者MB，内存大小规格化
    }

    /**
     * @param context
     * @return 手机当前可用内存(兆)
     */
    public static long getAvailMemory(Context context) {// 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem / 1024 / 1024;
    }

    /**
     * 版本名
     */
    public static String getVersionName(Context context) {
        PackageInfo pi = getPackageInfo(context);
        if (pi != null) {
            return pi.versionName;
        } else {
            return "";
        }
    }

    /**
     * 版本号
     */
    public static int getVersionCode(Context context) {
        PackageInfo pi = getPackageInfo(context);
        if (pi != null) {
            return pi.versionCode;
        } else {
            return 0;
        }
    }

    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    public static PackageInfo getPackageInfoForPermission(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_PERMISSIONS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    /**
     * @return 手机链接wifi的路由器的名字
     */
    public static String getWifiSSID(Context context) {
        WifiManager mWifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifi.isWifiEnabled()) {
            WifiInfo wifiInfo = mWifi.getConnectionInfo();
            return wifiInfo.getSSID();
        }
        return "";
    }

    /**
     * @return 手机链接wifi的路由器的mac地址
     */
    public static String getWifiBSSID(Context context) {
        WifiManager mWifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifi.isWifiEnabled()) {
            WifiInfo wifiInfo = mWifi.getConnectionInfo();
            return wifiInfo.getBSSID();
        }
        return "";
    }

    /**
     * @param permission
     * @param defalutValue 如果检测不到()检测发生异常的默认值
     * @return App是否拥有permission权限
     */
    public static boolean checkPermission(Context context, String permission, boolean defalutValue) {
        boolean permit = false;
        // 如果在判断的时候报错（有些机型），则默认不满足此权限
        try {
            permit = PackageManager.PERMISSION_GRANTED == context
                    .checkCallingOrSelfPermission(permission);
        } catch (Exception e) {
            permit = defalutValue;
        }
        return permit;
    }


    /**
     * 判断wifi是否开启
     *
     * @param context
     * @return
     */
    public static boolean isWifiEnabled(Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return (wm != null)
                && (wm.getWifiState() == WifiManager.WIFI_STATE_ENABLED || wm.getWifiState() == WifiManager.WIFI_STATE_ENABLING);
    }

    /**
     * Is wifi connected
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.isConnected()
                && (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public static boolean hasJellyBeanMr2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public static boolean hasJellyBeanMr1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * 判断是否为某品牌生产的手机
     */
    public static boolean isProduceByBrand(String brand) {
        boolean result = false;
        // 判断厂家
        if (Build.MANUFACTURER != null) {
            if (Build.MANUFACTURER.toLowerCase().contains(brand)) {
                result = true;
            }
        }
        // 判断品牌
        if (!result && Build.BRAND != null) {
            if (Build.BRAND.toLowerCase().contains(brand)) {
                result = true;
            }
        }
        return result;
    }

    public static boolean isProduceByXiaomi() {
        return isProductInBrands(BRAND_XIAOMI, BRAND_HONGMI);
    }

    public static boolean isProduceByHuaWei() {
        return isProductInBrands(BRAND_HUAWEI, BRAND_HONOR);
    }

    public static boolean isProduceBySamsung() {
        return isProduceByBrand(BRAND_SAMSUNG);
    }

    public static boolean isProductInBrands(String... brands) {
        for (String brand : brands) {
            if (isProduceByBrand(brand)) {
                return true;
            }
        }
        return false;
    }


    public static boolean isSimReady(Context context) {
        try {
            TelephonyManager mgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            Log.d(TAG, "[isSimReady]" + mgr.getSimState());
            return TelephonyManager.SIM_STATE_READY == mgr.getSimState()
                    || TelephonyManager.SIM_STATE_NETWORK_LOCKED == mgr.getSimState();
        } catch (Exception e) {
            Log.e(TAG, "[isSimReady]" + e);
        }
        return false;
    }

    /**
     * 判断当前手机是否有ROOT权限
     *
     * @return 是否ROOT
     */
    public static boolean isRoot(Context context) {
        if (ROOTED != null) {
            return ROOTED;
        }
        try {
            ROOTED = DeviceUtils.isDeviceRooted();
            if (ROOTED) {
                Log.w(TAG, "Device rooted.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Check root failed.", e);
            ROOTED = false;
        }
        return ROOTED;
    }

    public static boolean isDeviceRooted() {
        String su = "su";
        String[] locations = {"/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
                "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/",
                "/system/sbin/", "/usr/bin/", "/vendor/bin/"};
        for (String location : locations) {
            if (new File(location + su).exists()) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasFrontCamera() {
        int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (Camera.CameraInfo.CAMERA_FACING_FRONT == info.facing) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return Number of bytes available on external storage
     */
    public static long getExternalAvailableSpaceInBytes() {
        long availableSpace = -1L;
        try {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
                    .getPath());
            availableSpace = (long) stat.getAvailableBlocks()
                    * (long) stat.getBlockSize();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return availableSpace;
    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    private static String getSDTotalSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    private static String getSDAvailableSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }

    /**
     * 获得机身内存总大小
     *
     * @return
     */
    private static String getRomTotalSize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    private static String getRomAvailableSize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }


    public static String getSDCardSpace(Context context) {
        try {
            String free = getSDAvailableSize(context);
            String total = getSDTotalSize(context);
            return free + "/" + total;
        } catch (Exception e) {
            return "-/-";
        }
    }

    public static String getRomSpace(Context context) {
        try {
            String free = getRomAvailableSize(context);
            String total = getRomTotalSize(context);
            return free + "/" + total;
        } catch (Exception e) {
            return "-/-";
        }
    }

    public static int getWidthPixels(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return 0;
        }
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }


    public static int getRealHeightPixels(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = 0;
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        Class c;
        try {
            c = Class.forName("android.view.Display");
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            height = dm.heightPixels;
        } catch (Exception e) {
        }
        return height;
    }

    /**
     * 获取屏幕尺寸
     *
     * @param context
     * @return
     */
    public static double getScreenInch(Activity context) {
        double inch = 0;

        try {
            int realWidth = 0, realHeight = 0;
            Display display = context.getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            if (android.os.Build.VERSION.SDK_INT >= 17) {
                Point size = new Point();
                display.getRealSize(size);
                realWidth = size.x;
                realHeight = size.y;
            } else if (android.os.Build.VERSION.SDK_INT < 17
                    && android.os.Build.VERSION.SDK_INT >= 14) {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realWidth = (Integer) mGetRawW.invoke(display);
                realHeight = (Integer) mGetRawH.invoke(display);
            } else {
                realWidth = metrics.widthPixels;
                realHeight = metrics.heightPixels;
            }

            inch = formatDouble(Math.sqrt((realWidth / metrics.xdpi) * (realWidth / metrics.xdpi) + (realHeight / metrics.ydpi) * (realHeight / metrics.ydpi)), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return inch;
    }

    /**
     * Double类型保留指定位数的小数，返回double类型（四舍五入）
     * newScale 为指定的位数
     */
    private static double formatDouble(double d, int newScale) {
        BigDecimal bd = new BigDecimal(d);
        return bd.setScale(newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getIPAddress(final boolean useIPv4) {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            LinkedList<InetAddress> adds = new LinkedList<>();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp() || ni.isLoopback()) continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement());
                }
            }
            for (InetAddress add : adds) {
                if (!add.isLoopbackAddress()) {
                    String hostAddress = add.getHostAddress();
                    boolean isIPv4 = hostAddress.indexOf(':') < 0;
                    if (useIPv4) {
                        if (isIPv4) return hostAddress;
                    } else {
                        if (!isIPv4) {
                            int index = hostAddress.indexOf('%');
                            return index < 0
                                    ? hostAddress.toUpperCase()
                                    : hostAddress.substring(0, index).toUpperCase();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "null";
    }

}
