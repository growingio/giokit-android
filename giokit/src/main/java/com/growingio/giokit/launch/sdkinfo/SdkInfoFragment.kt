package com.growingio.giokit.launch.sdkinfo

import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.growingio.giokit.R
import com.growingio.giokit.launch.BaseFragment
import com.growingio.giokit.utils.SdkInfoUtils

/**
 * <p>
 *
 * @author cpacm 2021/8/26
 */
class SdkInfoFragment : BaseFragment() {

    private val sdkInfoAdapter: SdkInfoAdapter by lazy { SdkInfoAdapter(requireContext()) }
    override fun layoutId(): Int {
        return R.layout.fragment_gio_sdkinfo
    }

    override fun onViewCreated(view: View?) {
        val sdkList = findViewById<RecyclerView>(R.id.infoList)
        sdkList.layoutManager = LinearLayoutManager(requireContext())
        sdkList.adapter = sdkInfoAdapter
        sdkList.addItemDecoration(DividerItemDecoration(requireContext(), VERTICAL))

        val allInfos = arrayListOf<SdkInfo>()
        allInfos.addAll(SdkInfoUtils.getSdkInfo(requireContext()))
        allInfos.addAll(SdkInfoUtils.getAppInfo(requireContext()))
        allInfos.addAll(SdkInfoUtils.getMobileInfo(requireContext()))
        sdkInfoAdapter.setData(allInfos)
    }

    override fun onGetTitle(): String {
        return getString(R.string.gio_menu_info)
    }
}