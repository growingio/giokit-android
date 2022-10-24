package com.growingio.giokit.apm

import android.app.Application
import android.content.Context
import com.growingio.android.gmonitor.*
import com.growingio.android.gmonitor.anr.AnrIntegration
import com.growingio.android.gmonitor.crash.UncaughtExceptionHandlerIntegration
import com.growingio.android.gmonitor.fragment.FragmentLifecycleIntegration
import com.growingio.android.gmonitor.fragment.FragmentSupportLifecycleIntegration
import com.growingio.android.gmonitor.fragment.FragmentXLifecycleIntegration
import com.growingio.android.gmonitor.utils.AndroidLogger
import com.growingio.giokit.GioKitImpl


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

        if (context is Application) {
            option.integrations.add(ActivityLifecycleIntegration(context))
            option.integrations.add(FragmentXLifecycleIntegration(context))
            option.integrations.add(FragmentSupportLifecycleIntegration(context))
            option.integrations.add(FragmentLifecycleIntegration(context))
        }
        option.integrations.add(UncaughtExceptionHandlerIntegration())
        option.integrations.add(AnrIntegration(context))

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

        private var instance: GMonitorManager? = null

        @JvmStatic
        fun getInstance(): GMonitorManager =
            instance ?: synchronized(this) {
                instance ?: initGMonitor(GioKitImpl.APPLICATION).apply {
                    instance = this
                }
            }


        fun initGMonitor(application: Application): GMonitorManager {
            val option = GMonitorOption()
            option.debug = true
            option.enableActivityLifecycleTracing = true
            option.avoidRunningAppProcesses = false
            option.enableUncaughtExceptionHandler = true
            option.printUncaughtStackTrace = false
            option.enableFragmentXLifecycleTracing = true
            option.enableFragmentSupportLifecycleTracing = true
            option.enableFragmentSystemLifecycleTracing = false
            option.anrInDebug = true
            option.anrTimeoutIntervalMillis = 5000L
            option.logger = AndroidLogger()
            return GMonitorManager(application, option, GioKitTracker())
        }

    }
}