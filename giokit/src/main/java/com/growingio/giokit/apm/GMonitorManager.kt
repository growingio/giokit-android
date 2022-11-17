package com.growingio.giokit.apm

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager
import com.growingio.android.gmonitor.*
import com.growingio.android.gmonitor.anr.AnrIntegration
import com.growingio.android.gmonitor.crash.UncaughtExceptionHandlerIntegration
import com.growingio.android.gmonitor.fragment.FragmentSupportLifecycleIntegration
import com.growingio.android.gmonitor.fragment.FragmentXLifecycleIntegration
import com.growingio.android.gmonitor.utils.AndroidLogger
import com.growingio.giokit.GioKitImpl
import com.growingio.giokit.setting.SdkCommonSettingFragment.Companion.APM_ACTIVITY_ENABLED
import com.growingio.giokit.setting.SdkCommonSettingFragment.Companion.APM_ANR_ENABLED
import com.growingio.giokit.setting.SdkCommonSettingFragment.Companion.APM_FRAGMENT_ENABLED
import com.growingio.giokit.setting.SdkCommonSettingFragment.Companion.APM_JAVA_CRASH_ENABLED


/**
 * <p>
 *
 * @author cpacm 2022/10/19
 */
class GMonitorManager private constructor(
    private val context: Context,
    val option: GMonitorOption,
    val tracker: ITracker = GMonitorTracker()
) {

    private val currentTracker = ThreadLocal<ITracker>()

    init {
        currentTracker.set(tracker)

        with(option) {
            if (context is Application) {
                if (enableActivityLifecycleTracing) integrations.add(ActivityLifecycleIntegration(context))
                if (enableFragmentXLifecycleTracing) integrations.add(FragmentXLifecycleIntegration(context))
                if (enableFragmentSupportLifecycleTracing) integrations.add(FragmentSupportLifecycleIntegration(context))
            }
            if (enableUncaughtExceptionHandler) integrations.add(UncaughtExceptionHandlerIntegration())
            if (enableAnr) integrations.add(AnrIntegration(context))
        }


        option.integrations.forEach {
            it.register(getCurrentTracker(), option)
        }
    }

    private fun getCurrentTracker(): ITracker {
        var tracker = currentTracker.get()
        if (tracker == null) {
            tracker = this.tracker.clone()
            currentTracker.set(tracker)
        }
        return tracker
    }

    fun setUncaughtExceptionIntegration(enable: Boolean) {
        option.enableUncaughtExceptionHandler = enable
        var uncaughtException: Integration? = option.integrations.find {
            it is UncaughtExceptionHandlerIntegration
        }
        if (enable && uncaughtException == null) {
            uncaughtException = UncaughtExceptionHandlerIntegration()
            option.integrations.add(uncaughtException)
            uncaughtException.register(getCurrentTracker(), option)
        }
        if (!enable && uncaughtException != null) {
            uncaughtException.close()
            option.integrations.remove(uncaughtException)
        }
    }

    fun setAnrIntegration(enable: Boolean) {
        option.enableAnr = enable
        var anr: Integration? = option.integrations.find { it is AnrIntegration }
        if (enable && anr == null) {
            anr = AnrIntegration(context)
            option.integrations.add(anr)
            anr.register(getCurrentTracker(), option)
        }
        if (!enable && anr != null) {
            anr.close()
            option.integrations.remove(anr)
        }
    }

    fun setActivityIntegration(enable: Boolean) {
        option.enableActivityLifecycleTracing = enable
        var activity: Integration? = option.integrations.find { it is ActivityLifecycleIntegration }
        if (enable && activity == null && context is Application) {
            activity = ActivityLifecycleIntegration(context)
            option.integrations.add(activity)
            activity.register(getCurrentTracker(), option)
        }

        if (!enable && activity != null) {
            activity.close()
            option.integrations.remove(activity)
        }
    }

    fun setFragmentIntegration(enable: Boolean, isFragmentX: Boolean = true) {
        option.enableFragmentXLifecycleTracing = enable
        option.enableFragmentSupportLifecycleTracing = enable
        var fragment: Integration? = option.integrations.find {
            if (isFragmentX) it is FragmentXLifecycleIntegration
            else it is FragmentSupportLifecycleIntegration
        }

        if (enable && fragment == null && context is Application) {
            fragment =
                if (isFragmentX) FragmentXLifecycleIntegration(context) else FragmentSupportLifecycleIntegration(context)
            option.integrations.add(fragment)
            fragment.register(getCurrentTracker(), option)
        }
        if (!enable && fragment != null) {
            fragment.close()
            option.integrations.remove(fragment)
        }
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: GMonitorManager? = null

        @JvmStatic
        fun getInstance(application: Application? = null): GMonitorManager =
            instance ?: synchronized(this) {
                instance ?: initGMonitor(application ?: GioKitImpl.APPLICATION).apply {
                    instance = this
                }
            }


        fun initGMonitor(application: Application): GMonitorManager {
            val option = GMonitorOption()
            option.debug = true

            val sp = PreferenceManager.getDefaultSharedPreferences(application)

            option.enableActivityLifecycleTracing = sp.getBoolean(APM_ACTIVITY_ENABLED, true)
            option.avoidRunningAppProcesses = false
            option.enableUncaughtExceptionHandler = sp.getBoolean(APM_JAVA_CRASH_ENABLED, true)
            option.printUncaughtStackTrace = false
            option.enableFragmentXLifecycleTracing = sp.getBoolean(APM_FRAGMENT_ENABLED, true)
            option.enableFragmentSupportLifecycleTracing = sp.getBoolean(APM_FRAGMENT_ENABLED, true)
            option.enableFragmentSystemLifecycleTracing = false
            option.enableAnr = sp.getBoolean(APM_ANR_ENABLED, true)
            option.anrInDebug = true
            option.anrTimeoutIntervalMillis = 5000L
            option.logger = AndroidLogger()
            return GMonitorManager(application, option, GioKitTracker())
        }

    }
}