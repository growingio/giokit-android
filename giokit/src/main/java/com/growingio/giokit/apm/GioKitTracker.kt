package com.growingio.giokit.apm

import android.util.Log
import com.growingio.android.gmonitor.ITracker
import com.growingio.android.gmonitor.event.Breadcrumb
import com.growingio.giokit.GioKitImpl
import com.growingio.giokit.launch.db.BREADCRUMB_TYPE_ERROR
import com.growingio.giokit.launch.db.BREADCRUMB_TYPE_PERFORMANCE
import com.growingio.giokit.launch.db.GioKitBreadCrumb
import com.growingio.giokit.launch.db.GioKitDbManager
import com.growingio.giokit.utils.generateErrorFile

/**
 * <p>
 *
 * @author cpacm 2022/10/19
 */
class GioKitTracker : ITracker {
    override fun clone(): ITracker {
        return GioKitTracker()
    }

    override fun trackBreadcrumb(breadcrumb: Breadcrumb) {
        if (breadcrumb.type == Breadcrumb.TYPE_PERFORMANCE) {
            val category = breadcrumb.category
            val gioBc = GioKitBreadCrumb(
                0L, BREADCRUMB_TYPE_PERFORMANCE, "", "", "", "", 0L
            )
        } else if (breadcrumb.type == Breadcrumb.TYPE_ERROR) {
            val category = when (breadcrumb.category) {
                Breadcrumb.CATEGORY_ERROR_EXCEPTION -> "CRASH"
                Breadcrumb.CATEGORY_ERROR_ANR -> "ANR"
                Breadcrumb.CATEGORY_ERROR_HTTP -> "HTTP"
                else -> "CRASH"
            }
            val message = breadcrumb.data.get(Breadcrumb.ATTR_ERROR_TYPE) as String
            val content = breadcrumb.data.get(Breadcrumb.ATTR_ERROR_MESSAGE) as String
            val time = System.currentTimeMillis()

            val extraFilePath = if (!breadcrumb.message.isNullOrBlank()) {
                GioKitImpl.APPLICATION.generateErrorFile(
                    message + "-${time}.crash", breadcrumb.message!!
                )
            } else ""
            val errorBc = GioKitBreadCrumb(
                0L, BREADCRUMB_TYPE_ERROR, category, message, content, extraFilePath, time
            )
            GioKitDbManager.instance.insertBreadcrumb(errorBc)
        }
        Log.d("TRACKER", breadcrumb.toString())
    }
}