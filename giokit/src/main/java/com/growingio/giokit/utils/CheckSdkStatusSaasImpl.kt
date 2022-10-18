package com.growingio.giokit.utils

import com.growingio.android.sdk.collection.CoreInitialize
import com.growingio.android.sdk.collection.GConfig
import com.growingio.android.sdk.collection.GInternal
import com.growingio.android.sdk.collection.NetworkConfig
import com.growingio.giokit.hook.GioPluginConfig
import com.growingio.giokit.hover.check.CheckItem

/**
 * <p>
 *
 * @author cpacm 2022/8/10
 */
class CheckSdkStatusSaasImpl : CheckSdkStatusInterface {

    override fun getProjectStatus(index: Int): CheckItem {
        if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.collection.GInternal")) {
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

    override fun getURLScheme(index: Int): CheckItem {
        val xmlScheme = GioPluginConfig.xmlScheme
        if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.collection.CoreInitialize")) {
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

    override fun getDataSourceID(index: Int): CheckItem {
        if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.collection.CoreInitialize")) {
            if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.collection.ICfgCDPImpl")) {
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

    override fun getProjectID(index: Int): CheckItem {
        if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.collection.CoreInitialize")) {
            return CheckItem(
                index,
                "正在获取项目ID",
                "项目ID",
                CoreInitialize.coreAppState().getProjectId(),
                false
            )
        }
        return CheckItem(index, "正在获取项目ID", "项目ID", "未集成SDK", true)
    }

    override fun getDataServerHost(index: Int): CheckItem {
        if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.collection.NetworkConfig")) {
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

    override fun getDataCollectionEnable(index: Int): CheckItem {
        if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.collection.CoreInitialize")) {
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

    override fun getSdkDebug(index: Int): CheckItem {
        if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.collection.GConfig")) {
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

    override fun getOaidEnabled(index: Int): CheckItem {
        if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.collection.CoreInitialize")) {
            try {
                val oaid = CheckSdkStatusManager.getClassField(
                    CoreInitialize.deviceUUIDFactory(),
                    CoreInitialize.deviceUUIDFactory().javaClass.name,
                    "oaidEnable"
                )
                return CheckItem(
                    index,
                    "正在查询oaid状态",
                    "oaid采集",
                    if (oaid == null || !(oaid as Boolean)) "开" else "关",
                    false
                )
            } catch (e: Exception) {
            }
        }
        return CheckItem(index, "正在查询oaid状态", "oaid采集", "未集成", false)
    }
}