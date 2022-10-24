package com.growingio.giokit.setting

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.*
import com.growingio.giokit.R
import com.growingio.giokit.apm.GMonitorManager
import com.growingio.giokit.launch.db.GioKitDbManager
import com.growingio.giokit.utils.MeasureUtils.loadClass

/**
 * <p>
 *
 * @author cpacm 2022/8/5
 */
class SdkCommonSettingFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.common_setting_pref, rootKey)

        val clearEventPref: Preference? = findPreference("giokit_event_clear")
        clearEventPref?.setOnPreferenceClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.giokit_pref_event_clear)
                .setMessage(R.string.giokit_data_delete_message)
                .setPositiveButton(R.string.giokit_dialog_ok) { v, witch ->
                    GioKitDbManager.instance.cleanEvent()
                    v.dismiss()
                }.setNegativeButton(R.string.giokit_dialog_cancel) { v, witch ->
                    v.dismiss()
                }
                .show()
            true
        }

        val httpEventPref: Preference? = findPreference("giokit_http_clear")
        httpEventPref?.setOnPreferenceClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.giokit_pref_http_clear)
                .setMessage(R.string.giokit_http_delete_message)
                .setPositiveButton(R.string.giokit_dialog_ok) { v, witch ->
                    GioKitDbManager.instance.cleanHttp()
                    v.dismiss()
                }.setNegativeButton(R.string.giokit_dialog_cancel) { v, witch ->
                    v.dismiss()
                }
                .show()
            true
        }

        val apmDataPref: Preference? = findPreference("giokit_apm_clear")
        apmDataPref?.setOnPreferenceClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.giokit_pref_monitor_clear)
                .setMessage(R.string.giokit_apm_delete_message)
                .setPositiveButton(R.string.giokit_dialog_ok) { v, witch ->
                    GioKitDbManager.instance.cleanBreadcrumb()
                    v.dismiss()
                }.setNegativeButton(R.string.giokit_dialog_cancel) { v, witch ->
                    v.dismiss()
                }
                .show()
            true
        }


        val activityPref: SwitchPreference? = findPreference(APM_ACTIVITY_ENABLED)
        activityPref?.setOnPreferenceChangeListener { preference, newValue ->
            GMonitorManager.getInstance().setActivityIntegration(newValue as Boolean)
            true
        }

        val fragmentPref: SwitchPreference? = findPreference(APM_FRAGMENT_ENABLED)
        fragmentPref?.setOnPreferenceChangeListener { preference, newValue ->
            val hasFragmentX = loadClass("androidx.fragment.app.FragmentManager\$FragmentLifecycleCallbacks")
            GMonitorManager.getInstance()
                .setFragmentIntegration(newValue as Boolean, isFragmentX = hasFragmentX != null)
            true
        }

        val crashPref: SwitchPreference? = findPreference(APM_JAVA_CRASH_ENABLED)
        crashPref?.setOnPreferenceChangeListener { preference, newValue ->
            GMonitorManager.getInstance().setUncaughtExceptionIntegration(newValue as Boolean)
            true
        }

        val anrPref: SwitchPreference? = findPreference(APM_ANR_ENABLED)
        anrPref?.setOnPreferenceChangeListener { preference, newValue ->
            GMonitorManager.getInstance().setAnrIntegration(newValue as Boolean)
            true
        }
    }

    companion object {
        const val APM_ACTIVITY_ENABLED = "giokit_activity_time"
        const val APM_FRAGMENT_ENABLED = "giokit_fragment_time"
        const val APM_JAVA_CRASH_ENABLED = "giokit_crash_log"
        const val APM_ANR_ENABLED = "giokit_anr_log"
    }

}