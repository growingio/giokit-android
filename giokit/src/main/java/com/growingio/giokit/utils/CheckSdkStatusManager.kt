package com.growingio.giokit.utils

import android.view.View
import com.growingio.android.sdk.TrackerContext
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.android.sdk.autotrack.view.ViewAttributeUtil
import com.growingio.android.sdk.track.events.helper.DefaultEventFilterInterceptor
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

    fun getEventAlphaBet(eventType: String): String {
        return SdkV3InfoUtils.getEventAlphaBet(eventType)
    }

    fun getEventDesc(eventType: String, data: String): String {
        return SdkV3InfoUtils.getEventDesc(eventType, data)

    }

    fun ignoreViewClick(view: View) {
        ViewAttributeUtil.setIgnoreViewClick(view, true)
    }


    fun eventFilterProxy() {
        if (checkSdkInit()) {
            if (!hasClass("com.growingio.android.sdk.track.events.EventFilterInterceptor")) return
            val configurationProvider = GrowingAutotracker.get().context.configurationProvider
            if (configurationProvider != null) {
                val sdkEventFilter = configurationProvider.core().eventFilterInterceptor
                configurationProvider.core().eventFilterInterceptor =
                    GiokitEventFilterProxy(sdkEventFilter ?: DefaultEventFilterInterceptor())
            }
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
                    CheckSdkStatusV3Impl()
                ).apply {
                    instance = this
                }
            }

        fun checkSdkInit(): Boolean {
            if (hasClass("com.growingio.android.sdk.TrackerContext")) {
                return TrackerContext.initializedSuccessfully()
            }
            return false
        }

        fun hasClass(className: String): Boolean {
            return try {
                Class.forName(className)
                true
            } catch (e: ClassNotFoundException) {
                false
            }
        }

        fun hasMethodNoParam(obj: Any, className: String, method: String): Any? {
            return try {
                val clazz = Class.forName(className)
                val m = clazz.getDeclaredMethod(method)
                return m.invoke(obj)
            } catch (e: ClassNotFoundException) {
                null
            } catch (e: NoSuchMethodException) {
                null
            } catch (e: SecurityException) {
                null
            } catch (e: Exception) {
                null
            }
        }

        fun getClassField(obj: Any, className: String, field: String): Any? {
            return try {
                val clazz = Class.forName(className)
                val f = clazz.getDeclaredField(field)
                f.isAccessible = true
                return f.get(obj)
            } catch (e: ClassNotFoundException) {
                null
            } catch (e: NoSuchFieldException) {
                null
            } catch (e: SecurityException) {
                null
            } catch (e: Exception) {
                null
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
    fun getSdkModules(index: Int): CheckItem
}