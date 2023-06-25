package com.growingio.giokit.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import com.growingio.android.sdk.TrackerContext
import com.growingio.android.sdk.autotrack.AutotrackConfig
import com.growingio.android.sdk.track.events.AutotrackEventType
import com.growingio.android.sdk.track.events.TrackEventType
import com.growingio.android.sdk.track.middleware.http.EventEncoder
import com.growingio.android.sdk.track.providers.ConfigurationProvider
import com.growingio.android.sdk.track.providers.DeviceInfoProvider
import com.growingio.android.sdk.track.providers.SessionProvider
import com.growingio.android.sdk.track.providers.UserInfoProvider
import com.growingio.giokit.hook.GioPluginConfig
import com.growingio.giokit.launch.sdkinfo.SdkInfo
import org.json.JSONException
import org.json.JSONObject

/**
 * <p>
 *
 * @author cpacm 2021/8/26
 */
object SdkV3InfoUtils {
    //获取 sdk 信息
    fun getSdkInfo(): List<SdkInfo> {
        val list = arrayListOf<SdkInfo>()
        list.add(SdkInfo("GrowingIO SDK信息", isHeader = true))
        val hasDepend = hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")
        if (hasDepend) {
            val (_, sdkVersion, _) = GioPluginConfig.analyseDepend()
            list.tryAdd { SdkInfo("SDK版本", sdkVersion) }
            list.tryAdd { SdkInfo("项目ID", ConfigurationProvider.core().projectId) }
            list.tryAdd { SdkInfo("URLScheme", ConfigurationProvider.core().urlScheme) }
            val checkItem = CheckSdkStatusManager.getInstance().getDataSourceID(0)
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
                    "集成模块",
                    CheckSdkStatusManager.getInstance().getSdkModules(0).content
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
            val scale = getImpressionScale()
            if (scale >= 0F) list.tryAdd { SdkInfo("曝光比例", scale.toString()) }
            list.tryAdd { SdkInfo("数据加密", getEncryptEnabled()) }
            list.tryAdd { SdkInfo("Imei开关", ConfigurationProvider.core().isDebugEnabled.toString()) }

            list.tryAdd { SdkInfo("登录账户", getLoginUser()) }
            list.tryAdd { SdkInfo("位置信息", getLocation()) }
            list.tryAdd { SdkInfo("设备ID", DeviceInfoProvider.get().deviceId) }
        } else {
            list.add(SdkInfo("SDK", "SDK未集成"))
        }

        return list
    }

    private fun getEncryptEnabled(): String {
        if (TrackerContext.get().registry.getModelLoader(EventEncoder::class.java) != null) {
            return "启用"
        }
        return "未启用"
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
            val userKey = UserInfoProvider.get().loginUserKey
            var userInfo = "未配置"
            if (!userKey.isNullOrEmpty() && !userId.isNullOrEmpty()) userInfo = "$userKey-$userId"
            else if (!userId.isNullOrEmpty()) userInfo = userId
            return userInfo
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

    fun getEventAlphaBet(eventType: String): String {
        return when (eventType) {
            TrackEventType.VISITOR_ATTRIBUTES -> "VA"
            TrackEventType.LOGIN_USER_ATTRIBUTES -> "UA"
            TrackEventType.CONVERSION_VARIABLES -> "CV"
            TrackEventType.APP_CLOSED -> "A"
            AutotrackEventType.PAGE_ATTRIBUTES -> "PA"
            AutotrackEventType.VIEW_CLICK -> "CK"
            AutotrackEventType.VIEW_CHANGE -> "CG"
            TrackEventType.FORM_SUBMIT -> "FS"
            TrackEventType.REENGAGE -> "RG"
            TrackEventType.ACTIVATE -> "AV"
            else -> eventType.first().uppercase()
        }
    }

    fun getEventDesc(eventType: String, data: String): String {
        try {
            val jsonObj = JSONObject(data)
            // custom name
            val name = jsonObj.optString("eventName")
            if (name.isNotEmpty()) return name

            // page
            val p = jsonObj.optString("path")
            if (p.isNotEmpty()) return p

            // visit
            if (eventType == TrackEventType.VISIT) {
                val userId = jsonObj.optString("userId")
                if (userId.isNotEmpty()) return userId
                val oaid = jsonObj.optString("oaid")
                if (oaid.isNotEmpty()) return oaid
                val adrid = jsonObj.optString("androidId")
                if (adrid.isNotEmpty()) return adrid
                return jsonObj.optString("domain")
            }

            if (eventType == TrackEventType.APP_CLOSED) {
                return jsonObj.optString("timestamp")
            }
            return jsonObj.optString("appName")

        } catch (e: JSONException) {
        }
        return data
    }
}