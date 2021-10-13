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
import com.growingio.android.sdk.track.events.helper.EventExcludeFilter
import com.growingio.android.sdk.track.events.helper.FieldIgnoreFilter
import com.growingio.android.sdk.track.providers.ConfigurationProvider
import com.growingio.android.sdk.track.providers.DeviceInfoProvider
import com.growingio.android.sdk.track.providers.SessionProvider
import com.growingio.android.sdk.track.providers.UserInfoProvider
import com.growingio.giokit.hook.GioPluginConfig
import com.growingio.giokit.launch.sdkinfo.SdkInfo

/**
 * <p>
 *
 * @author cpacm 2021/8/26
 */
object SdkV3InfoUtils {
    //获取 sdk 信息
    fun getSdkInfo(context: Context): List<SdkInfo> {
        val list = arrayListOf<SdkInfo>()
        list.add(SdkInfo("GrowingIO SDK信息", isHeader = true))
        val hasDepend = hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")
        if (hasDepend) {
            val (_, sdkVersion, _) = GioPluginConfig.analyseDepend()
            list.tryAdd { SdkInfo("SDK版本", sdkVersion) }
            list.tryAdd { SdkInfo("ProjectId", ConfigurationProvider.core().projectId) }
            list.tryAdd { SdkInfo("URLScheme", ConfigurationProvider.core().urlScheme) }
            val checkItem = CheckSelfUtils.getDataSourceID(0)
            list.tryAdd { SdkInfo("DataSource ID", checkItem.content) }
            list.tryAdd {
                SdkInfo(
                    "DataServerHost",
                    ConfigurationProvider.core().dataCollectionServerHost
                )
            }
            list.tryAdd {
                SdkInfo(
                    "数据收集",
                    if (ConfigurationProvider.core().isDataCollectionEnabled) "打开" else "关闭"
                )
            }
            list.tryAdd {
                SdkInfo(
                    "Debug测试",
                    if (ConfigurationProvider.core().isDebugEnabled) "是" else "否"
                )
            }
            list.tryAdd {
                SdkInfo(
                    "oaid采集",
                    CheckSelfUtils.getOaidEnabled(0).content
                )
            }
            list.tryAdd { SdkInfo("分发渠道", ConfigurationProvider.core().channel) }
            list.tryAdd {
                SdkInfo(
                    "每日流量限制",
                    ConfigurationProvider.core().cellularDataLimit.toString() + "M"
                )
            }
            list.tryAdd {
                SdkInfo(
                    "数据发送间隔",
                    ConfigurationProvider.core().dataUploadInterval.toString() + "S"
                )
            }
            list.tryAdd {
                SdkInfo(
                    "访问会话时长",
                    ConfigurationProvider.core().sessionInterval.toString() + "S"
                )
            }
            list.tryAdd { SdkInfo("事件过滤", getExcludeEvent()) }
            list.tryAdd { SdkInfo("事件属性过滤", getIgnoreFiled()) }
            val scale = getImpressionScale()
            if (scale >= 0F) list.tryAdd { SdkInfo("曝光比例", scale.toString()) }

            list.tryAdd { SdkInfo("登录账户", getLoginUser()) }
            list.tryAdd { SdkInfo("位置信息", getLocation()) }
            list.tryAdd { SdkInfo("设备ID", DeviceInfoProvider.get().deviceId) }
        } else {
            list.add(SdkInfo("SDK", "SDK未集成"))
        }

        return list
    }

    private fun getExcludeEvent(): String {
        val excludeEventMask = ConfigurationProvider.core().excludeEvent
        val events = EventExcludeFilter.getEventFilterLog(excludeEventMask)
        if (events.isNullOrEmpty()) return "未设置"
        return events.substringAfter("[").substringBefore("]")
    }

    private fun getIgnoreFiled(): String {
        val ignoreMask = ConfigurationProvider.core().ignoreField
        val fields = FieldIgnoreFilter.getFieldFilterLog(ignoreMask)
        if (fields.isNullOrEmpty()) return "未设置"
        return fields.substringAfter("[").substringBefore("]")
    }

    private fun getImpressionScale(): Float {
        if (hasClass("com.growingio.android.sdk.autotrack.AutotrackConfig")) {
            val config = ConfigurationProvider.get()
                .getConfiguration<AutotrackConfig>(AutotrackConfig::class.java)
            return config.impressionScale

        }
        return -1F
    }

    private fun getLoginUser(): String {
        if (hasClass("com.growingio.android.sdk.track.providers.UserInfoProvider")) {
            val userId = UserInfoProvider.get().loginUserId
            return if (userId.isNullOrEmpty()) "未配置" else userId
        }
        return "未配置"
    }

    private fun getLocation(): String {
        if (hasClass("com.growingio.android.sdk.track.providers.SessionProvider")) {
            val la = SessionProvider.get().latitude
            val lo = SessionProvider.get().longitude
            if (la == 0.0 && lo == 0.0) return "未配置"
            return "$la,$lo"
        }
        return "未配置"
    }

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
        list.tryAdd { SdkInfo("SD卡剩余空间", DeviceUtils.getSDCardSpace(context)) }
        list.tryAdd { SdkInfo("系统剩余空间", DeviceUtils.getRomSpace(context)) }
        list.tryAdd {
            SdkInfo(
                "分辨率",
                "${DeviceUtils.getWidthPixels(context)}x${
                    DeviceUtils.getRealHeightPixels(
                        context
                    )
                }"
            )
        }
        if (context is Activity) {
            list.tryAdd { SdkInfo("屏幕尺寸", DeviceUtils.getScreenInch(context).toString()) }
        }
        list.tryAdd { SdkInfo("ROOT", DeviceUtils.isRoot(context).toString()) }
        list.tryAdd { SdkInfo("DENSITY", Resources.getSystem().displayMetrics.density.toString()) }
        list.tryAdd { SdkInfo("IP", DeviceUtils.getIPAddress(true)) }
        if (hasClass("com.growingio.android.sdk.track.providers.DeviceInfoProvider")) {
            list.tryAdd { SdkInfo("IMEI", DeviceInfoProvider.get().deviceId) }
        }
        if (hasClass("com.growingio.android.sdk.track.providers.DeviceInfoProvider")) {
            list.tryAdd { SdkInfo("AndroidId", DeviceInfoProvider.get().androidId) }
        }
        if (hasClass("com.growingio.android.sdk.track.providers.DeviceInfoProvider")) {
            DeviceInfoProvider.get().googleAdId?.let {
                list.tryAdd { SdkInfo("GoogleId", it) }
            }
        }
        return list
    }

    fun ignoreActivity(activity: Activity) {
        if (hasClass("com.growingio.android.sdk.autotrack.page.PageProvider")) {
            PageProvider.get().addIgnoreActivity(activity, IgnorePolicy.IGNORE_ALL)
        }
    }
}