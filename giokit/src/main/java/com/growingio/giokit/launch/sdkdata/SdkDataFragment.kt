package com.growingio.giokit.launch.sdkdata

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
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
class SdkDataFragment : BaseFragment() {

    private val sdkDataAdapter by lazy { SdkDataAdapter(requireContext()) { onEventClick(it) } }

    private val swipeLayout: SwipeRefreshLayout by lazy { findViewById(R.id.swipeLayout) }

    override fun layoutId(): Int {
        return R.layout.fragment_gio_sdkdata
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val eventFlow = Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false,
                initialLoadSize = 30
            ),
            pagingSourceFactory = {
                SdkDataSource()
            }
        ).flow

        lifecycleScope.launch {
            eventFlow.collectLatest {
                sdkDataAdapter.submitData(it)
            }
        }

    }

    fun onEventClick(id: Int) {
        SdkDataInfoFragment.newInstance(id).show(childFragmentManager, "eventdata")
    }

    override fun onViewCreated(view: View?) {
        val sdkList = findViewById<RecyclerView>(R.id.dataList)
        sdkList.layoutManager = LinearLayoutManager(requireContext())
        sdkList.adapter = sdkDataAdapter
        sdkList.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
        sdkDataAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Loading -> {
                    swipeLayout.isRefreshing = true
                }
                is LoadState.NotLoading -> {
                    swipeLayout.isRefreshing = false
                }
                is LoadState.Error -> {
                    swipeLayout.isRefreshing = false
                }
            }
        }

        swipeLayout.setOnRefreshListener {
            sdkDataAdapter.refresh()
        }

    }

    override fun onGetTitle(): String {
        return getString(R.string.gio_menu_code)
    }
}