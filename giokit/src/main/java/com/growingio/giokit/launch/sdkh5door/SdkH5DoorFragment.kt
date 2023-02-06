package com.growingio.giokit.launch.sdkh5door

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.growingio.giokit.R
import com.growingio.giokit.hook.GioPluginConfig
import com.growingio.giokit.launch.BaseFragment
import com.growingio.giokit.launch.UniversalActivity

/**
 * <p>
 *
 * @author cpacm 2023/2/1
 */
class SdkH5DoorFragment : BaseFragment() {
    override fun layoutId(): Int {
        return R.layout.fragment_giokit_h5door
    }

    override fun onViewCreated(view: View?) {
        val inputEt = findViewById<EditText>(android.R.id.text1)
        val hybridEnabled = findViewById<CheckBox>(R.id.hybridEnabled)
        val jsGioEnabled = findViewById<CheckBox>(R.id.webGiokitEnabled)
        val jumpBtn = findViewById<Button>(R.id.linkJump)
        if(GioPluginConfig.isSaasSdk){
            jsGioEnabled.isEnabled = false
        }

        jumpBtn.setOnClickListener {
            val url = inputEt.editableText?.toString()
            if (url == null || url.isEmpty()) {
                loadWebView(
                    getString(R.string.giokit_menu_h5door_input),
                    hybridEnabled.isChecked,
                    jsGioEnabled.isChecked
                )
                return@setOnClickListener
            }
            if (!url.startsWith("http")) {
                Toast.makeText(requireContext(), R.string.giokit_menu_h5door_error, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loadWebView(url, hybridEnabled.isChecked, jsGioEnabled.isChecked)
        }
    }

    fun loadWebView(url: String, enableHybrid: Boolean, enableJsGiokit: Boolean) {
        if (activity is UniversalActivity) {
            (activity as UniversalActivity).showContent(
                SdkH5Fragment::class.java,
                Bundle().apply {
                    putString("url", url)
                    putBoolean("hybrid_enabled", enableHybrid)
                    putBoolean("giokit_enabled", enableJsGiokit)
                })
        }
    }

    override fun onGetTitle(): String {
        return getString(R.string.giokit_menu_h5door)
    }
}