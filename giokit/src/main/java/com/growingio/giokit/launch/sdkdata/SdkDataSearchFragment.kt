package com.growingio.giokit.launch.sdkdata

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.growingio.giokit.R
import com.growingio.giokit.launch.BaseFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * <p>
 *
 * @author cpacm 2021/8/30
 */
class SdkDataSearchFragment : BaseFragment() {

    private val sdkDataAdapter by lazy { SdkDataAdapter(requireContext()) { onEventClick(it) } }

    override fun layoutId(): Int {
        return R.layout.fragment_giokit_sdkdata_search
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments?.getString("event").isNullOrEmpty()) finish()
    }

    fun onEventClick(id: Int) {
        SdkDataInfoFragment.newInstance(id).show(childFragmentManager, "eventsearch")
    }

    override fun onViewCreated(view: View?) {
        val sdkList = findViewById<RecyclerView>(R.id.dataList)
        sdkList.layoutManager = LinearLayoutManager(requireContext())
        sdkList.adapter = sdkDataAdapter
        sdkList.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))

        lifecycleScope.launch {
            val eventFlow = Pager(
                config = PagingConfig(
                    pageSize = 50,
                    enablePlaceholders = false,
                    initialLoadSize = 20
                ),
                pagingSourceFactory = {
                    SdkDataSearchSource(arguments?.getString("event") ?: "")
                }
            ).flow
            eventFlow.collectLatest {
                sdkDataAdapter.submitData(it)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        finish()
        return true
    }

    override fun onGetTitle(): String {
        val event = arguments?.getString("event")?:""
        return getString(R.string.giokit_menu_data_search, event)
    }
}