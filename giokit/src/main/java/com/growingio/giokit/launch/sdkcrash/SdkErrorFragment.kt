package com.growingio.giokit.launch.sdkcrash

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
class SdkErrorFragment : BaseFragment() {

    private val sdkErrorAdapter by lazy { SdkErrorAdapter { onErrorClick(it) } }

    override fun layoutId(): Int {
        return R.layout.fragment_giokit_sdkerror
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View?) {
        val sdkList = findViewById<RecyclerView>(R.id.httpList)
        sdkList.layoutManager = LinearLayoutManager(requireContext())
        sdkErrorAdapter.withLoadStateFooter(LoadingMoreAdapter())
        sdkList.adapter = sdkErrorAdapter

        lifecycleScope.launch {
            val httpFlow = Pager(
                config = PagingConfig(50, enablePlaceholders = false, initialLoadSize = 20),
                pagingSourceFactory = { SdkErrorSource() }
            ).flow
            httpFlow.collectLatest { sdkErrorAdapter.submitData(it) }
        }

    }

    private fun onErrorClick(id: Long) {
        SdkErrorLogFragment.newInstance(id).show(childFragmentManager, "errorlog")
    }


    override fun onGetTitle(): String {
        return getString(R.string.giokit_menu_error)
    }

}