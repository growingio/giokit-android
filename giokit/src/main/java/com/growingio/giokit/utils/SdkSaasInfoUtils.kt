package com.growingio.giokit.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import com.growingio.android.sdk.autotrack.AutotrackConfig
import com.growingio.android.sdk.autotrack.IgnorePolicy
import com.growingio.android.sdk.autotrack.page.PageProvider
import com.growingio.android.sdk.collection.CoreInitialize
import com.growingio.android.sdk.collection.GConfig
import com.growingio.android.sdk.collection.GrowingIO
import com.growingio.android.sdk.collection.NetworkConfig
import com.growingio.android.sdk.track.events.helper.EventExcludeFilter
import com.growingio.android.sdk.track.events.helper.FieldIgnoreFilter
import com.growingio.android.sdk.track.providers.ConfigurationProvider
import com.growingio.android.sdk.track.providers.DeviceInfoProvider
import com.growingio.android.sdk.track.providers.SessionProvider
import com.growingio.android.sdk.track.providers.UserInfoProvider
import com.growingio.giokit.hook.GioPluginConfig
import com.growingio.giokit.launch.sdkinfo.SdkInfo
import java.util.*

/**
 * <p>
 *
 * @author cpacm 2021/8/26
 */
object SdkSaasInfoUtils {
    //获取 sdk 信息
    fun getSdkInfo(context: Context): List<SdkInfo> {
        val list = arrayListOf<SdkInfo>()
        list.add(SdkInfo("GrowingIO SDK信息", isHeader = true))
        val hasDepend = hasClass("com.growingio.android.sdk.collection.CoreInitialize")
        if (hasDepend) {
            val (_, sdkVersion, _) = GioPluginConfig.analyseDepend()
            list.add(SdkInfo("SDK版本", sdkVersion))
            list.add(SdkInfo("ProjectId", CoreInitialize.coreAppState().projectId))
            list.add(SdkInfo("URLScheme", CoreInitialize.config().getsGrowingScheme()))
            list.add(SdkInfo("DataServerHost", NetworkConfig.getInstance().endPoint))

            val sdkInfoStrArray = CoreInitialize.config().toString().split("\n")
            for (info in sdkInfoStrArray) {
                val infoKv = info.split(":")
                if (infoKv.size > 1) {
                    getMappingInfo(infoKv[0], infoKv[1])?.let {
                        list.add(it)
                    }
                }
            }
            list.add(SdkInfo("登录账户", CoreInitialize.growingIOIPC().userId))
            list.add(
                SdkInfo(
                    "位置信息",
                    "${CoreInitialize.coreAppState().latitude},${CoreInitialize.coreAppState().longitude}"
                )
            )


        } else {
            list.add(SdkInfo("SDK", "SDK未集成"))
        }

        return list
    }

    private fun getMappingInfo(info: String, value: String): SdkInfo? {
        val msg = info.trim().lowercase(Locale.getDefault())
        if (infoMapping.containsKey(msg)) {
            return SdkInfo(infoMapping.getOrElse(msg) { "未定" }, value.trim())
        }
        return null
    }

    private val infoMapping = hashMapOf<String, String>(
        Pair("debug", "调试模式"),
        Pair("enabled", "数据收集"),
        Pair("user_id", "路径控件id"),
        Pair("test mode", "测试模式"),
        Pair("upload bulk size", "缓存数据最多(条)"),
        Pair("flush interval", "数据刷新间隔(ms)"),
        Pair("session interval", "后台停留最长(ms)"),
        Pair("channel", "渠道"),
        Pair("track all fragments", "fragment发送page"),
        Pair("enable webview", "WebView 混合模式"),
        Pair("enable hashtag", "HashTag"),
        Pair("cellular data limit", "移动网络下流量限制"),
        Pair("total cellular data size", "移动网络下已发送流量"),
        Pair("sampling", "视图采样率"),
        Pair("enable impression", "是否采集imp事件"),
        Pair("throttle", "是否节流发送"),
        Pair("disable cellular impression", "关闭移动网络下imp事件"),
    )

    private fun hasClass(className: String): Boolean {
        try {
            Class.forName(className)
            return true
        } catch (e: ClassNotFoundException) {
            return false
        }
    }

    fun getAppInfo(context: Context): List<SdkInfo> {
        val list = arrayListOf<SdkInfo>()
        val pi = getPackageInfo(context)
        if (pi != null) {
            list.add(SdkInfo("App信息", isHeader = true))
            list.add(SdkInfo("包名", pi.packageName))
            list.add(SdkInfo("应用版本名", pi.versionName))
            list.add(SdkInfo("应用版本号", pi.versionCode.toString()))
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                list.add(SdkInfo("最低系统版本号", context.applicationInfo.minSdkVersion.toString()))
            }
            list.add(SdkInfo("目标系统版本号", context.applicationInfo.targetSdkVersion.toString()))
        }
        return list
    }

    private fun getPackageInfo(context: Context): PackageInfo? {
        var pi: PackageInfo? = null
        try {
            val pm = context.packageManager
            pi = pm.getPackageInfo(
                context.packageName,
                PackageManager.GET_CONFIGURATIONS
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return pi
    }

    fun getMobileInfo(context: Context): List<SdkInfo> {
        val list = arrayListOf<SdkInfo>()
        list.add(SdkInfo("设备信息", isHeader = true))
        list.add(SdkInfo("手机型号", Build.MANUFACTURER + " " + Build.MODEL))
        list.add(SdkInfo("系统版本", Build.VERSION.RELEASE + " (" + Build.VERSION.SDK_INT + ")"))
        try {
            list.add(SdkInfo("SD卡剩余空间", DeviceUtils.getSDCardSpace(context)))
        } catch (e: java.lang.Exception) {
            e.printStackTrace();
        }
        try {
            list.add(SdkInfo("系统剩余空间", DeviceUtils.getRomSpace(context)))
        } catch (e: java.lang.Exception) {
            e.printStackTrace();
        }

        try {
            list.add(
                SdkInfo(
                    "分辨率",
                    "${DeviceUtils.getWidthPixels(context)}x${
                        DeviceUtils.getRealHeightPixels(
                            context
                        )
                    }"
                )
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace();
        }

        try {
            if (context is Activity) {
                list.add(SdkInfo("屏幕尺寸", DeviceUtils.getScreenInch(context).toString()))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace();
        }

        try {
            list.add(SdkInfo("ROOT", DeviceUtils.isRoot(context).toString()))
        } catch (e: java.lang.Exception) {
            e.printStackTrace();
        }

        try {
            list.add(SdkInfo("DENSITY", Resources.getSystem().displayMetrics.density.toString()))
        } catch (e: java.lang.Exception) {
            e.printStackTrace();
        }

        try {
            list.add(SdkInfo("IP", DeviceUtils.getIPAddress(true)))
        } catch (e: java.lang.Exception) {
            e.printStackTrace();
        }

        try {
            if (hasClass("com.growingio.android.sdk.collection.CoreInitialize")) {
                list.add(SdkInfo("IMEI", CoreInitialize.deviceUUIDFactory().deviceId))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace();
        }

        try {
            if (hasClass("com.growingio.android.sdk.collection.CoreInitialize")) {
                list.add(SdkInfo("AndroidId", CoreInitialize.deviceUUIDFactory().androidId))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace();
        }

        try {
            if (hasClass("com.growingio.android.sdk.collection.CoreInitialize")) {
                CoreInitialize.deviceUUIDFactory().googleAdId?.let {
                    list.add(SdkInfo("GoogleId", it))
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace();
        }

        return list
    }
}