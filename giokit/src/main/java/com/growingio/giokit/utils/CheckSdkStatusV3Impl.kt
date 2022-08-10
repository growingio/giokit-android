package com.growingio.giokit.utils

import com.growingio.android.sdk.TrackerContext
import com.growingio.android.sdk.autotrack.CdpAutotrackConfig
import com.growingio.android.sdk.track.CdpConfig
import com.growingio.android.sdk.track.middleware.OaidHelper
import com.growingio.android.sdk.track.providers.ConfigurationProvider
import com.growingio.giokit.hook.GioPluginConfig
import com.growingio.giokit.hover.check.CheckItem

/**
 * <p>
 *
 * @author cpacm 2022/8/10
 */
class CheckSdkStatusV3Impl : CheckSdkStatusInterface {
    override fun getProjectStatus(index: Int): CheckItem {
        if (CheckSelfUtils.hasClass("com.growingio.android.sdk.TrackerContext")) {
            val hasInited = TrackerContext.get() != null
            val lazyInit = GioPluginConfig.isInitLazy
            return CheckItem(
                index,
                "正在获取SDK初始化状态",
                "初始化",
                if (hasInited) {
                    if (lazyInit) "已初始化(延迟)" else "已初始化"
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

    override fun getURLScheme(index: Int): CheckItem {
        val xmlScheme = GioPluginConfig.xmlScheme
        if (CheckSelfUtils.hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
            val urlScheme = ConfigurationProvider.core().urlScheme
            // 插件未找到 urlscheme 时不做校验
            if (xmlScheme.isNullOrEmpty() || urlScheme == xmlScheme) {
                return CheckItem(
                    index,
                    "正在校对URL Scheme",
                    "URL Scheme",
                    urlScheme,
                    false
                )
            }
            if (!xmlScheme.startsWith("growing.")) {
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
        }
        return CheckItem(
            index,
            "正在校对URL Scheme",
            "URL Scheme", "未集成SDK", true
        )
    }

    override fun getDataSourceID(index: Int): CheckItem {
        if (CheckSelfUtils.hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
            if (CheckSelfUtils.hasClass("com.growingio.android.sdk.autotrack.CdpAutotrackConfig")) {
                val config = ConfigurationProvider.get()
                    .getConfiguration<CdpAutotrackConfig>(CdpAutotrackConfig::class.java)
                return CheckItem(
                    index,
                    "正在获取数据源ID",
                    "Datasource ID",
                    config?.dataSourceId ?: "未配置",
                    config?.dataSourceId == null
                )
            } else if (CheckSelfUtils.hasClass("com.growingio.android.sdk.track.CdpConfig")) {
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

    override fun getProjectID(index: Int): CheckItem {
        if (CheckSelfUtils.hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
            return CheckItem(
                index,
                "正在获取项目ID",
                "项目ID",
                ConfigurationProvider.core().projectId,
                false
            )
        }
        return CheckItem(index, "正在获取项目ID", "项目ID", "未集成SDK", true)
    }

    override fun getDataServerHost(index: Int): CheckItem {
        if (CheckSelfUtils.hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
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

    override fun getDataCollectionEnable(index: Int): CheckItem {
        if (CheckSelfUtils.hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
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

    override fun getSdkDebug(index: Int): CheckItem {
        if (CheckSelfUtils.hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
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

    override fun getOaidEnabled(index: Int): CheckItem {
        if (CheckSelfUtils.hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
            val isV320 = CheckSelfUtils.hasMethodNoParam(
                ConfigurationProvider.core(),
                "com.growingio.android.sdk.CoreConfiguration",
                "isOaidEnabled"
            )
            if (isV320 != null) {
                if (isV320 is Boolean) {
                    return CheckItem(
                        index,
                        "正在查询oaid状态",
                        "oaid采集",
                        if (isV320) "开" else "关",
                        false
                    )
                }
            }
        }
        if (CheckSelfUtils.hasClass("com.growingio.android.sdk.TrackerContext") && CheckSelfUtils.hasClass("com.growingio.android.sdk.track.middleware.OaidHelper")) {
            //v3.3.0 oaid转为模块
            return with(TrackerContext.get().registry.getModelLoader(OaidHelper::class.java)) {
                CheckItem(
                    index,
                    "正在查询oaid状态",
                    "oaid采集模块",
                    if (this != null) "已注册" else "未注册",
                    false
                )
            }
        }
        return CheckItem(index, "正在查询oaid状态", "oaid采集", "未集成SDK", true)
    }
}