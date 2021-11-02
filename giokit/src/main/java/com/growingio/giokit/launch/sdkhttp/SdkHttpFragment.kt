package com.growingio.giokit.launch.sdkhttp

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.growingio.giokit.R
import com.growingio.giokit.launch.BaseFragment
import com.growingio.giokit.launch.db.GioKitDbManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * <p>
 *
 * @author cpacm 2021/10/30
 */
class SdkHttpFragment : BaseFragment() {

    private val sdkHttpAdapter by lazy { SdkHttpAdapter { onHttpClick(it) } }
    private val sdkHttpHeaderAdapter by lazy { SdkHttpHeaderAdapter() }
    private var httpFetch = true

    override fun layoutId(): Int {
        return R.layout.fragment_giokit_sdkhttp
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            flow {
                val tick = HttpStatusBean(0, 0, 0L, 0)
                while (httpFetch) {
                    tick.tick++
                    if (tick.tick % 5 == 1) {
                        val count = GioKitDbManager.instance.countRunningRequest()
                        if (count > tick.count) {
                            tick.update = true
                        }
                        tick.count = count
                        tick.size = GioKitDbManager.instance.sumUploadDataSize()
                        tick.error = GioKitDbManager.instance.countRunningErrorRequest()
                    }
                    delay(1000)
                    emit(tick)
                }
            }.collect {
                if (it.update) {
                    sdkHttpAdapter.refresh()
                    it.update = false
                }
                sdkHttpHeaderAdapter.setData(it)
            }
        }
    }

    override fun onViewCreated(view: View?) {
        val sdkList = findViewById<RecyclerView>(R.id.httpList)
        sdkList.layoutManager = LinearLayoutManager(requireContext())
        val concatAdapter = ConcatAdapter(sdkHttpHeaderAdapter, sdkHttpAdapter)
        sdkList.adapter = concatAdapter
        sdkHttpAdapter.addLoadStateListener { }

        lifecycleScope.launch {
            val httpFlow = Pager(
                config = PagingConfig(30, enablePlaceholders = false, initialLoadSize = 20),
                pagingSourceFactory = { SdkHttpSource() }
            ).flow
            httpFlow.collectLatest { sdkHttpAdapter.submitData(it) }
        }

    }

    override fun onDestroy() {
        httpFetch = false
        super.onDestroy()
    }

    private fun onHttpClick(id: Int) {
        SdkHttpDetailFragment.newInstance(id).show(childFragmentManager, "httpdata")
    }


    override fun onGetTitle(): String {
        return getString(R.string.giokit_menu_http)
    }

}