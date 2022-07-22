package com.growingio.giokit.launch.sdkdata

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.growingio.giokit.R
import com.growingio.giokit.launch.BaseFragment
import com.growingio.giokit.launch.LoadingMoreAdapter
import com.growingio.giokit.launch.UniversalActivity
import com.growingio.giokit.launch.db.GioKitDbManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * <p>
 *
 * @author cpacm 2021/8/30
 */
class SdkDataFragment : BaseFragment() {

    private val sdkDataAdapter by lazy { SdkDataAdapter(requireContext()) { onEventClick(it) } }
    private val sdkHeaderAdapter by lazy {
        SdkHttpHeaderAdapter(object : SdkSearchView.OnSearchListener {
            override fun onSearch(keyword: String) {
                onEventSearch(keyword)
            }
        })
    }

    private val swipeLayout: SwipeRefreshLayout by lazy { findViewById(R.id.swipeLayout) }
    private val fab: FloatingActionButton by lazy { findViewById(R.id.fab) }

    override fun layoutId(): Int {
        return R.layout.fragment_giokit_sdkdata
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun onEventClick(id: Int) {
        SdkDataInfoFragment.newInstance(id).show(childFragmentManager, "eventdata")
    }

    override fun onViewCreated(view: View?) {
        val sdkList = findViewById<RecyclerView>(R.id.dataList)
        sdkList.layoutManager = LinearLayoutManager(requireContext())
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
        sdkDataAdapter.withLoadStateFooter(LoadingMoreAdapter())

        sdkList.adapter = ConcatAdapter(sdkHeaderAdapter, sdkDataAdapter)
        swipeLayout.setOnRefreshListener {
            sdkDataAdapter.refresh()
        }

        lifecycleScope.launch {
            val eventFlow = Pager(
                config = PagingConfig(
                    pageSize = 50,
                    enablePlaceholders = false,
                    initialLoadSize = 20
                ),
                pagingSourceFactory = {
                    SdkDataSource()
                }
            ).flow
            eventFlow.collectLatest {
                sdkDataAdapter.submitData(it)
            }
        }

        fab.setOnClickListener {
            AlertDialog.Builder(requireContext()).setMessage(R.string.giokit_data_delete_message)
                .setPositiveButton(R.string.giokit_dialog_ok) { _, _ ->
                    GioKitDbManager.instance.cleanEvent()
                    sdkDataAdapter.refresh()
                }
                .setNegativeButton(R.string.giokit_dialog_cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
        }
    }

    fun onEventSearch(keyword: String) {
        if (activity is UniversalActivity) {
            (activity as UniversalActivity).showContent(
                SdkDataSearchFragment::class.java,
                Bundle().apply { putString("event", keyword) })
        }
    }

    override fun onGetTitle(): String {
        return getString(R.string.giokit_menu_data)
    }
}