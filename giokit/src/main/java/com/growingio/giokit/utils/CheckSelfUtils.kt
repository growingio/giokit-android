package com.growingio.giokit.utils

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import com.growingio.android.sdk.TrackerContext
import com.growingio.android.sdk.autotrack.CdpAutotrackConfig
import com.growingio.android.sdk.track.CdpConfig
import com.growingio.android.sdk.track.providers.ConfigurationProvider
import com.growingio.giokit.hook.GioPluginConfig
import com.growingio.giokit.hook.GioTrackInfo
import com.growingio.giokit.hover.check.CheckItem

/**
 * <p>
 *
 * @author cpacm 2021/8/16
 */
object CheckSelfUtils {
    @JvmStatic
    fun getDeviceInfo(context: Context, index: Int): CheckItem {
        val sb = StringBuilder()
        sb.append("设备:").append(Build.MANUFACTURER).append("-").append(Build.BRAND).append("\n")
            .append("系统版本:Android ").append(Build.VERSION.RELEASE).append(", SDK ")
            .append(Build.VERSION.SDK_INT).append("\n")
            .append("网络状态:").append(if (getActiveNetworkState(context)) "联网" else "未联网")
        return CheckItem(index, "正在获取设备信息", "设备信息", sb.toString())
    }

    private fun getActiveNetworkState(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        return if (networkInfo == null) {
            false
        } else {
            networkInfo.isConnected
        }
    }

    @JvmStatic
    fun getSdkDepend(index: Int): CheckItem {
        val (title, content, isError) = GioPluginConfig.analyseDepend()
        return CheckItem(index, "正在获取SDK版本", title, content, isError)
    }

    @JvmStatic
    fun hasSdkPlugin(index: Int): CheckItem {
        val (content, isError) = GioPluginConfig.hasSdkPlugin()
        return CheckItem(index, "正在获取SDK插件", "SDK插件", content, isError)
    }

    @JvmStatic
    fun getProjectStatus(index: Int): CheckItem {
        if (hasClass("com.growingio.android.sdk.TrackerContext")) {
            val hasInited = TrackerContext.get() != null
            val lazyInit = GioPluginConfig.isInitLazy
            return CheckItem(
                index,
                "正在获取SDK初始化状态",
                "初始化",
                if (hasInited) {
                    if (lazyInit) "已初始化(延迟)" else "初始化"
                } else {
                    "未初始化"
                },
                !hasInited || lazyInit
            )
        }
        return CheckItem(
            index,
            "正在获取SDK初始化状态",
            "初始化", "未集成SDK", true
        )
    }

    @JvmStatic
    fun getURLScheme(index: Int): CheckItem {
        val xmlScheme = GioPluginConfig.xmlScheme
        if (hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
            val urlScheme = ConfigurationProvider.core().urlScheme
            if (xmlScheme == null) {
                return CheckItem(
                    index,
                    "正在校对URLScheme",
                    "URL Scheme",
                    "AndroidManifest 中未配置URL Scheme",
                    true
                )
            }
            if (urlScheme != xmlScheme) {
                return CheckItem(
                    index,
                    "正在校对URLScheme",
                    "URL Scheme",
                    "AndroidManifest与初始化传入的URL Scheme不匹配",
                    true
                )
            }
            if (urlScheme == xmlScheme) {
                return CheckItem(
                    index,
                    "正在校对URL Scheme",
                    "URL Scheme",
                    urlScheme,
                    false
                )
            }

        }
        return CheckItem(
            index,
            "正在校对URL Scheme",
            "URL Scheme", "未集成SDK", true
        )
    }

    @JvmStatic
    fun getDataSourceID(index: Int): CheckItem {
        if (hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
            if (hasClass("com.growingio.android.sdk.autotrack.CdpAutotrackConfig")) {
                val config = ConfigurationProvider.get()
                    .getConfiguration<CdpAutotrackConfig>(CdpAutotrackConfig::class.java)
                return CheckItem(
                    index,
                    "正在获取数据源ID",
                    "Datasource ID",
                    config?.dataSourceId ?: "未配置",
                    config?.dataSourceId == null
                )
            } else if (hasClass("com.growingio.android.sdk.track.CdpConfig")) {
                val config = ConfigurationProvider.get()
                    .getConfiguration<CdpConfig>(CdpConfig::class.java)
                return CheckItem(
                    index,
                    "正在获取数据源ID",
                    "Datasource ID",
                    config?.dataSourceId ?: "未配置",
                    config?.dataSourceId == null
                )
            }
            return CheckItem(
                index,
                "正在获取数据源ID",
                "Datasource ID",
                "非Cdp无需配置",
                false
            )
        }
        return CheckItem(
            index,
            "正在获取数据源ID",
            "Datasource ID",
            "未集成SDK",
            true
        )
    }

    @JvmStatic
    fun getProjectID(index: Int): CheckItem {
        if (hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
            return CheckItem(
                index,
                "正在获取Project ID",
                "Project ID",
                ConfigurationProvider.core().projectId,
                false
            )
        }
        return CheckItem(index, "正在获取Project ID", "Project ID", "未集成SDK", true)
    }

    @JvmStatic
    fun getDataServerHost(index: Int): CheckItem {
        if (hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
            return with(ConfigurationProvider.core().dataCollectionServerHost) {
                CheckItem(
                    index,
                    "正在获取ServerHost",
                    "ServerHost",
                    if (isNullOrEmpty()) "未配置" else this,
                    isNullOrEmpty()
                )
            }
        }
        return CheckItem(index, "正在获取ServerHost", "ServerHost", "未集成SDK", true)
    }

    @JvmStatic
    fun getDataCollectionEnable(index: Int): CheckItem {
        if (hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
            return with(ConfigurationProvider.core().isDataCollectionEnabled) {
                CheckItem(
                    index,
                    "正在获取数据采集是否打开",
                    "数据采集",
                    if (this) "打开" else "关闭",
                    !this
                )
            }
        }
        return CheckItem(index, "正在获取数据收集是否打开", "数据收集", "未集成SDK", true)
    }

    @JvmStatic
    fun getSdkDebug(index: Int): CheckItem {
        if (hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
            return with(ConfigurationProvider.core().isDebugEnabled) {
                CheckItem(
                    index,
                    "正在处于Debug调试模式",
                    "调试模式",
                    if (this) "是" else "否",
                    this
                )
            }
        }
        return CheckItem(index, "正在处于Debug调试模式", "调试模式", "未集成SDK", true)
    }

    @JvmStatic
    fun getOaidEnabled(index: Int): CheckItem {
        if (hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
            return with(ConfigurationProvider.core().isOaidEnabled) {
                CheckItem(
                    index,
                    "正在查询oaid状态",
                    "oaid采集",
                    if (this) "开" else "关",
                    false
                )
            }
        }
        return CheckItem(index, "正在查询oaid状态", "oaid采集", "未集成SDK", true)
    }

    @JvmStatic
    fun getTrackCount(index: Int): CheckItem {
        return CheckItem(
            index,
            "正在获取手动埋点数目",
            "手动埋点",
            "共有 ${GioTrackInfo.trackList.size} 处（不包括自动埋点）",
            GioTrackInfo.trackList.size <= 0
        )
    }

    fun checkSdkInit(): Boolean {
        if (hasClass("com.growingio.android.sdk.TrackerContext")) {
            val hasInited = TrackerContext.get() != null
            return hasInited
        }
        return false
    }

    private fun hasClass(className: String): Boolean {
        try {
            Class.forName(className)
            return true
        } catch (e: ClassNotFoundException) {
            return false
        }
    }
}