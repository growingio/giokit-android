package com.growingio.giokit.launch.sdkcode

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.growingio.giokit.R
import com.growingio.giokit.hook.GioTrackInfo
import com.growingio.giokit.launch.BaseFragment

/**
 * <p>
 *
 * @author cpacm 2021/8/30
 */
class SdkCodeFragment :BaseFragment() {

    private val sdkCodeAdapter by lazy { SdkCodeAdapter(requireContext()) }

    override fun layoutId(): Int {
        return R.layout.fragment_giokit_sdkcode
    }

    override fun onViewCreated(view: View?) {
        val sdkList = findViewById<RecyclerView>(R.id.codeList)
        sdkList.layoutManager = LinearLayoutManager(requireContext())
        sdkList.adapter = sdkCodeAdapter

        sdkCodeAdapter.setData(dealWithTrackCode(GioTrackInfo.trackList))

    }

    override fun onGetTitle(): String {
        return getString(R.string.giokit_menu_code)
    }
}