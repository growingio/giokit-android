package com.growingio.giokit.apm

import android.util.Log
import androidx.annotation.StringRes
import com.growingio.android.gmonitor.ITracker
import com.growingio.android.gmonitor.event.Breadcrumb
import com.growingio.android.gmonitor.event.Breadcrumb.Companion.CATEGORY_PERFORMANCE_ACTIVITY
import com.growingio.android.gmonitor.event.Breadcrumb.Companion.CATEGORY_PERFORMANCE_APP
import com.growingio.android.gmonitor.event.Breadcrumb.Companion.CATEGORY_PERFORMANCE_FRAGMENT
import com.growingio.giokit.GioKitImpl
import com.growingio.giokit.R
import com.growingio.giokit.launch.db.BREADCRUMB_TYPE_ERROR
import com.growingio.giokit.launch.db.BREADCRUMB_TYPE_PERFORMANCE
import com.growingio.giokit.launch.db.GioKitBreadCrumb
import com.growingio.giokit.launch.db.GioKitDbManager
import com.growingio.giokit.utils.NotificationUtils
import com.growingio.giokit.utils.NotificationUtils.IMPORTANCE_HIGH
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
        if (ignoreBreadcrumb(breadcrumb)) return
        if (breadcrumb.type == Breadcrumb.TYPE_PERFORMANCE) {
            var category = breadcrumb.category
            if (category == CATEGORY_PERFORMANCE_APP) {
                category = "APP"
                val duration = breadcrumb.data.get(Breadcrumb.ATTR_PERFORMANCE_APP_DURATION) as Long
                val isCold = breadcrumb.data.get(Breadcrumb.ATTR_PERFORMANCE_APP_COLD) as Boolean
                val message = getString(if (isCold) R.string.giokit_pref_app_cold else R.string.giokit_pref_app_warm)
                GioKitDbManager.instance.insertBreadcrumb(
                    GioKitBreadCrumb(
                        0L,
                        BREADCRUMB_TYPE_PERFORMANCE,
                        category,
                        category,
                        message,
                        duration,
                        "",
                        System.currentTimeMillis()
                    )
                )
            } else if (category == CATEGORY_PERFORMANCE_ACTIVITY) {
                category = "ACTIVITY"
                val pageName = breadcrumb.data.get(Breadcrumb.ATTR_PERFORMANCE_PAGE_NAME).toString()
                val fullPageName = breadcrumb.data.get(Breadcrumb.ATTR_PERFORMANCE_PAGE_FULLNAME).toString()
                val lastFullPageName = breadcrumb.data.get(Breadcrumb.ATTR_PERFORMANCE_LASTPAGE_FULLNAME).toString()
                val content = "$lastFullPageName -> $fullPageName"
                val duration = breadcrumb.data.get(Breadcrumb.ATTR_PERFORMANCE_DURATION) as Long
                val isCold = breadcrumb.data.get(Breadcrumb.ATTR_PERFORMANCE_APP_COLD)
                if (isCold != null && !(isCold as Boolean)) {
                    GioKitDbManager.instance.insertBreadcrumb(
                        GioKitBreadCrumb(
                            0L,
                            BREADCRUMB_TYPE_PERFORMANCE,
                            category,
                            category,
                            getString(R.string.giokit_pref_app_warm),
                            duration,
                            "",
                            System.currentTimeMillis()
                        )
                    )
                    return
                }
                GioKitDbManager.instance.insertBreadcrumb(
                    GioKitBreadCrumb(
                        0L,
                        BREADCRUMB_TYPE_PERFORMANCE,
                        category,
                        pageName,
                        content,
                        duration,
                        "",
                        System.currentTimeMillis()
                    )
                )
            } else if (category == CATEGORY_PERFORMANCE_FRAGMENT) {
                category = "FRAGMENT"
                val pageName = breadcrumb.data.get(Breadcrumb.ATTR_PERFORMANCE_PAGE_NAME).toString()
                val fullPageName = breadcrumb.data.get(Breadcrumb.ATTR_PERFORMANCE_PAGE_FULLNAME).toString()
                val lastFullPageName = breadcrumb.data.get(Breadcrumb.ATTR_PERFORMANCE_LASTPAGE_FULLNAME).toString()
                val content = "$lastFullPageName $$ $fullPageName"
                val duration = breadcrumb.data.get(Breadcrumb.ATTR_PERFORMANCE_DURATION) as Long
                GioKitDbManager.instance.insertBreadcrumb(
                    GioKitBreadCrumb(
                        0L,
                        BREADCRUMB_TYPE_PERFORMANCE,
                        category,
                        pageName,
                        content,
                        duration,
                        "",
                        System.currentTimeMillis()
                    )
                )
            }
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
                0L, BREADCRUMB_TYPE_ERROR, category, message, content, 0L, extraFilePath, time
            )
            GioKitDbManager.instance.insertBreadcrumb(errorBc)
            NotificationUtils.notify(500, NotificationUtils.ChannelConfig("crash", "crash", IMPORTANCE_HIGH)) {
                it.setContentTitle(message.uppercase())
                    .setContentText(content)
                    .setSmallIcon(R.drawable.ic_giokit_g)
                    .build()
            }
        }
        Log.d("TRACKER", breadcrumb.toString())
    }

    fun getString(@StringRes stringRes: Int, vararg args: Any): String {
        val context = GioKitImpl.APPLICATION
        return context.getString(stringRes, args)
    }

    private fun ignoreBreadcrumb(breadcrumb: Breadcrumb): Boolean {
        if (breadcrumb.type == Breadcrumb.TYPE_PERFORMANCE) {
            val category = breadcrumb.category
            if (category == CATEGORY_PERFORMANCE_ACTIVITY || category == CATEGORY_PERFORMANCE_FRAGMENT) {
                val fullPageName = breadcrumb.data.get(Breadcrumb.ATTR_PERFORMANCE_PAGE_FULLNAME)?.toString()
                if (fullPageName != null && Regex("^com.growingio.giokit.(launch|setting)").containsMatchIn(fullPageName)) {
                    return true
                }
            }

        }
        return false
    }

}