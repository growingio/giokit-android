package com.growingio.giokit.utils

import com.growingio.android.sdk.TrackerContext
import com.growingio.android.sdk.track.middleware.OaidHelper
import com.growingio.android.sdk.track.middleware.advert.Activate
import com.growingio.android.sdk.track.middleware.format.EventFormatData
import com.growingio.android.sdk.track.middleware.http.EventEncoder
import com.growingio.android.sdk.track.middleware.hybrid.HybridBridge
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
        if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.TrackerContext")) {
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
        if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
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
        if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
            if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.autotrack.CdpAutotrackConfig")) {
                val dataSourceId = ConfigurationProvider.core().dataSourceId
                return CheckItem(
                    index,
                    "正在获取数据源ID",
                    "Datasource ID",
                    dataSourceId ?: "未配置",
                    dataSourceId == null
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
        if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
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
        if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
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
        if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
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
        if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.track.providers.ConfigurationProvider")) {
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

    override fun getSdkModules(index: Int): CheckItem {
        val modules = arrayListOf<String>()
        if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.TrackerContext")) {
            // oaid模块
            if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.track.middleware.OaidHelper")) {
                with(TrackerContext.get().registry.getModelLoader(OaidHelper::class.java)) {
                    if (this != null) modules.add("oaid")
                }

            }


            // protobuf 模块
            if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.track.middleware.format.EventFormatData")) {
                with(TrackerContext.get().registry.getModelLoader(EventFormatData::class.java)) {
                    if (this != null && this.javaClass.name == "com.growingio.protobuf.ProtobufDataLoader") {
                        modules.add("protobuf")
                    }
                }
            }

            // hybrid
            if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.track.middleware.hybrid.HybridBridge")) {
                with(TrackerContext.get().registry.getModelLoader(HybridBridge::class.java)) {
                    if (this != null && this.javaClass.name == "com.growingio.android.hybrid.HybridBridgeLoader") {
                        modules.add("hybrid")
                    }
                }
            }

            // encoder
            if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.track.middleware.http.EventEncoder")) {
                with(TrackerContext.get().registry.getModelLoader(EventEncoder::class.java)) {
                    if (this != null && this.javaClass.name == "com.growingio.android.encoder.EncoderDataLoader") {
                        modules.add("加密")
                    }
                }
            }

            // debugger
            if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.track.webservices.Debugger")) {
                if (CheckSdkStatusManager.hasClass("com.growingio.android.debugger.DebuggerDataLoader")) {
                    modules.add("debugger")
                }
            }

            // circler
            if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.track.webservices.Circler")) {
                if (CheckSdkStatusManager.hasClass("com.growingio.android.circler.CirclerDataLoader")) {
                    modules.add("圈选")
                }
            }

            // advert
            if (CheckSdkStatusManager.hasClass("com.growingio.android.sdk.track.middleware.advert.Activate")) {
                with(TrackerContext.get().registry.getModelLoader(Activate::class.java)) {
                    if (this != null && this.javaClass.name == "com.growingio.android.advert.AdvertActivateDataLoader") {
                        modules.add("广告")
                    }
                }
            }
            val checkItem = CheckItem(
                index, "正在查询集成模块", "已集成模块", modules.joinToString("，"), false
            )

            return checkItem
        }

        return CheckItem(index, "正在查询集成模块", "集成模块", "未集成模块", false)
    }
}