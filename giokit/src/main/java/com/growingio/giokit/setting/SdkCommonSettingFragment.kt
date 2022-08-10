package com.growingio.giokit.setting

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.growingio.giokit.R
import com.growingio.giokit.launch.db.GioKitDbManager

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
    }

}