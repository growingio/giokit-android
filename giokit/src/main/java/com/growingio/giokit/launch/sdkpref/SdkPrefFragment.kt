package com.growingio.giokit.launch.sdkpref

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.growingio.giokit.R
import com.growingio.giokit.launch.BaseFragment
import com.growingio.giokit.launch.LoadingMoreAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * <p>
 *
 * @author cpacm 2022/10/20
 */
class SdkPrefFragment : BaseFragment() {

    private val sdkPrefAdapter by lazy { SdkPrefAdapter() }
    private val swipeLayout: SwipeRefreshLayout by lazy { findViewById(R.id.swipeLayout) }

    override fun layoutId(): Int {
        return R.layout.fragment_giokit_sdkpref
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View?) {
        val sdkList = findViewById<RecyclerView>(R.id.prefList)
        sdkList.layoutManager = LinearLayoutManager(requireContext())
        sdkPrefAdapter.withLoadStateFooter(LoadingMoreAdapter())
        sdkList.adapter = sdkPrefAdapter
        sdkPrefAdapter.addLoadStateListener {
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
        lifecycleScope.launch {
            val prefFlow = Pager(
                config = PagingConfig(50, enablePlaceholders = false, initialLoadSize = 20),
                pagingSourceFactory = { SdkPrefSource() }
            ).flow
            prefFlow.collectLatest { sdkPrefAdapter.submitData(it) }
        }

        swipeLayout.setOnRefreshListener {
            sdkPrefAdapter.refresh()
        }
    }

    override fun onResume() {
        super.onResume()
        sdkPrefAdapter.refresh()
    }


    override fun onGetTitle(): String {
        return getString(R.string.giokit_menu_pref)
    }

}