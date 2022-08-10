package com.growingio.giokit.utils

import com.growingio.android.sdk.track.events.helper.DefaultEventFilterInterceptor
import com.growingio.android.sdk.track.providers.ConfigurationProvider
import com.growingio.giokit.hook.GioPluginConfig
import com.growingio.giokit.hook.GioTrackInfo
import com.growingio.giokit.hover.check.CheckItem

/**
 * <p>
 *
 * @author cpacm 2022/8/10
 */
class CheckSdkStatusManager private constructor(val checkSdkStatus: CheckSdkStatusInterface) :
    CheckSdkStatusInterface by checkSdkStatus {

    fun eventFilterProxy() {
        if (!GioPluginConfig.isSaasSdk && CheckSelfUtils.checkSdkInit()) {
            val sdkEventFilter = ConfigurationProvider.core().eventFilterInterceptor;
            ConfigurationProvider.core()
                .setEventFilterInterceptor(GiokitEventFilterProxy(sdkEventFilter ?: DefaultEventFilterInterceptor()))
        }
    }

    fun getSdkDepend(index: Int): CheckItem {
        val (title, content, isError) = GioPluginConfig.analyseDepend()
        return CheckItem(index, "正在获取SDK版本", title, content, isError)
    }

    fun hasSdkPlugin(index: Int): CheckItem {
        val (content, isError) = GioPluginConfig.hasSdkPlugin()
        return CheckItem(index, "正在获取SDK插件", "SDK插件", content, isError)
    }

    fun getTrackCount(index: Int): CheckItem {
        return CheckItem(
            index,
            "正在获取手动埋点数目",
            "手动埋点",
            "共有 ${GioTrackInfo.trackList.size} 处（不包括自动埋点）",
            GioTrackInfo.trackList.size <= 0
        )
    }

    companion object {
        private var instance: CheckSdkStatusManager? = null

        @JvmStatic
        fun getInstance(): CheckSdkStatusManager =
            instance ?: synchronized(this) {
                instance ?: CheckSdkStatusManager(
                    if (GioPluginConfig.isSaasSdk) {
                        CheckSdkStatusSaasImpl()
                    } else {
                        CheckSdkStatusV3Impl()
                    }
                ).apply {
                    instance = this
                }
            }
    }
}

interface CheckSdkStatusInterface {
    fun getProjectStatus(index: Int): CheckItem
    fun getURLScheme(index: Int): CheckItem
    fun getDataSourceID(index: Int): CheckItem
    fun getProjectID(index: Int): CheckItem
    fun getDataServerHost(index: Int): CheckItem
    fun getDataCollectionEnable(index: Int): CheckItem
    fun getSdkDebug(index: Int): CheckItem
    fun getOaidEnabled(index: Int): CheckItem
}