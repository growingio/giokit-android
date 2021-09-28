package com.growingio.giokit.utils

import com.growingio.android.sdk.TrackerContext
import com.growingio.android.sdk.autotrack.CdpAutotrackConfig
import com.growingio.android.sdk.collection.*
import com.growingio.android.sdk.track.CdpConfig
import com.growingio.android.sdk.track.providers.ConfigurationProvider
import com.growingio.giokit.hook.GioPluginConfig
import com.growingio.giokit.hook.GioTrackInfo
import com.growingio.giokit.hover.check.CheckItem
import java.lang.Exception

/**
 * <p>
 *
 * @author cpacm 2021/8/16
 */
object CheckSelfUtils {

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
        if (GioPluginConfig.isSaasSdk) {
            return getProjectStatusSaas(index)
        } else {
            return getProjectStatusV3(index)
        }
    }


    private fun getProjectStatusV3(index: Int): CheckItem {
        if (hasClass("com.growingio.android.sdk.TrackerContext")) {
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


    private fun getProjectStatusSaas(index: Int): CheckItem {
        if (hasClass("com.growingio.android.sdk.collection.GInternal")) {
            val hasInited = !GInternal.getInstance().featuresVersionJson.isNullOrEmpty()
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


    @JvmStatic
    fun getURLScheme(index: Int): CheckItem {
        if (GioPluginConfig.isSaasSdk) {
            return getURLSchemeSaas(index)
        } else {
            return getURLSchemeV3(index)
        }
    }

    private fun getURLSchemeV3(index: Int): CheckItem {
        val xmlScheme = GioPluginConfig.xmlScheme
        if (hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
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

    private fun getURLSchemeSaas(index: Int): CheckItem {
        val xmlScheme = GioPluginConfig.xmlScheme
        if (hasClass("com.growingio.android.sdk.collection.CoreInitialize")) {
            val urlScheme = CoreInitialize.config().getsGrowingScheme()
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

    @JvmStatic
    fun getDataSourceID(index: Int): CheckItem {
        if (GioPluginConfig.isSaasSdk) {
            return getDataSourceIDSaas(index)
        } else {
            return getDataSourceIDV3(index)
        }
    }

    private fun getDataSourceIDSaas(index: Int): CheckItem {
        if (hasClass("com.growingio.android.sdk.collection.CoreInitialize")) {
            if (hasClass("com.growingio.android.sdk.collection.ICfgCDPImpl")) {
                val coreAppState = CoreInitialize.coreAppState()
                val coreClass = coreAppState.javaClass
                try {
                    val dataSourceID =
                        coreClass.getDeclaredMethod("getDataSourceId").invoke(coreAppState)
                    return CheckItem(
                        index,
                        "正在获取数据源ID",
                        "Datasource ID",
                        dataSourceID?.toString() ?: "未配置",
                        dataSourceID == null
                    )
                } catch (e: Exception) {
                }
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

    private fun getDataSourceIDV3(index: Int): CheckItem {
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
        if (GioPluginConfig.isSaasSdk) {
            return getProjectIDSaas(index)
        } else {
            return getProjectIDV3(index)
        }
    }

    private fun getProjectIDV3(index: Int): CheckItem {
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

    private fun getProjectIDSaas(index: Int): CheckItem {
        if (hasClass("com.growingio.android.sdk.collection.CoreInitialize")) {
            return CheckItem(
                index,
                "正在获取Project ID",
                "Project ID",
                CoreInitialize.coreAppState().getProjectId(),
                false
            )
        }
        return CheckItem(index, "正在获取Project ID", "Project ID", "未集成SDK", true)
    }

    @JvmStatic
    fun getDataServerHost(index: Int): CheckItem {
        if (GioPluginConfig.isSaasSdk) {
            return getDataServerHostSaas(index)
        } else {
            return getDataServerHostV3(index)
        }
    }


    private fun getDataServerHostV3(index: Int): CheckItem {
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

    private fun getDataServerHostSaas(index: Int): CheckItem {
        if (hasClass("com.growingio.android.sdk.collection.NetworkConfig")) {
            return with(NetworkConfig.getInstance().apiEndPoint()) {
                CheckItem(
                    index,
                    "正在获取Data Host",
                    "DataHost",
                    if (isNullOrEmpty()) "未配置" else this,
                    isNullOrEmpty()
                )
            }
        }
        return CheckItem(index, "正在获取DataHost", "DataHost", "未集成SDK", true)
    }

    @JvmStatic
    fun getDataCollectionEnable(index: Int): CheckItem {
        if (GioPluginConfig.isSaasSdk) {
            return getDataCollectionEnableSaas(index)
        } else {
            return getDataCollectionEnableV3(index)
        }
    }

    private fun getDataCollectionEnableV3(index: Int): CheckItem {
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

    private fun getDataCollectionEnableSaas(index: Int): CheckItem {
        if (hasClass("com.growingio.android.sdk.collection.CoreInitialize")) {
            return with(CoreInitialize.config().isEnabled) {
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
        if (GioPluginConfig.isSaasSdk) {
            return getSdkDebugSaas(index)
        } else {
            return getSdkDebugV3(index)
        }
    }

    private fun getSdkDebugV3(index: Int): CheckItem {
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

    private fun getSdkDebugSaas(index: Int): CheckItem {
        if (hasClass("com.growingio.android.sdk.collection.GConfig")) {
            return with(GConfig.DEBUG) {
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
        if (GioPluginConfig.isSaasSdk) {
            return getOaidEnabledSaas(index)
        } else {
            return getOaidEnabledV3(index)
        }
    }


    private fun getOaidEnabledV3(index: Int): CheckItem {
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

    private fun getOaidEnabledSaas(index: Int): CheckItem {
        if (hasClass("com.growingio.android.sdk.collection.CoreInitialize")) {
            try {
                val oaid = CoreInitialize.deviceUUIDFactory().oaid
                return CheckItem(
                    index,
                    "正在查询oaid状态",
                    "oaid采集",
                    if (oaid.isEmpty()) "未获取" else "已获取",
                    false
                )
            } catch (e: Exception) {
            }
        }
        return CheckItem(index, "正在查询oaid状态", "oaid采集", "未集成", false)
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
            return TrackerContext.get() != null
        }
        if (hasClass("com.growingio.android.sdk.collection.GInternal")) {
            return !GInternal.getInstance().featuresVersionJson.isNullOrEmpty()
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