package com.growingio.giokit.utils

import com.growingio.android.sdk.TrackerContext
import com.growingio.android.sdk.autotrack.CdpAutotrackConfig
import com.growingio.android.sdk.collection.*
import com.growingio.android.sdk.track.CdpConfig
import com.growingio.android.sdk.track.middleware.OaidHelper
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

    fun checkSdkInit(): Boolean {
        if (hasClass("com.growingio.android.sdk.TrackerContext")) {
            return TrackerContext.initializedSuccessfully()
        }
        if (hasClass("com.growingio.android.sdk.collection.GInternal")) {
            return !GInternal.getInstance().featuresVersionJson.isNullOrEmpty()
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