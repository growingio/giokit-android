package com.growingio.giokit.launch.sdkinfo

import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.growingio.giokit.R
import com.growingio.giokit.hook.GioPluginConfig
import com.growingio.giokit.launch.BaseFragment
import com.growingio.giokit.utils.SdkSaasInfoUtils
import com.growingio.giokit.utils.SdkV3InfoUtils

/**
 * <p>
 *
 * @author cpacm 2021/8/26
 */
class SdkInfoFragment : BaseFragment() {

    private val sdkInfoAdapter: SdkInfoAdapter by lazy { SdkInfoAdapter(requireContext()) }
    override fun layoutId(): Int {
        return R.layout.fragment_giokit_sdkinfo
    }

    override fun onViewCreated(view: View?) {
        val sdkList = findViewById<RecyclerView>(R.id.infoList)
        sdkList.layoutManager = LinearLayoutManager(requireContext())
        sdkList.adapter = sdkInfoAdapter
        sdkList.addItemDecoration(DividerItemDecoration(requireContext(), VERTICAL))

        val allInfos = arrayListOf<SdkInfo>()
        if (GioPluginConfig.isSaasSdk) {
            allInfos.addAll(SdkSaasInfoUtils.getSdkInfo(requireContext()))
            allInfos.addAll(SdkSaasInfoUtils.getAppInfo(requireContext()))
            allInfos.addAll(SdkSaasInfoUtils.getMobileInfo(requireContext()))
        } else {
            allInfos.addAll(SdkV3InfoUtils.getSdkInfo(requireContext()))
            allInfos.addAll(SdkV3InfoUtils.getAppInfo(requireContext()))
            allInfos.addAll(SdkV3InfoUtils.getMobileInfo(requireContext()))
        }
        sdkInfoAdapter.setData(allInfos)
    }

    override fun onGetTitle(): String {
        return getString(R.string.giokit_menu_info)
    }
}