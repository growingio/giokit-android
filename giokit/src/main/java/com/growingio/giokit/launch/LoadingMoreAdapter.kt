package com.growingio.giokit.launch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.growingio.giokit.R

/**
 * <p>
 *
 * @author cpacm 2021/11/3
 */
class LoadingMoreAdapter() : LoadStateAdapter<LoadMoreViewHolder>() {
    override fun onBindViewHolder(holder: LoadMoreViewHolder, loadState: LoadState) {
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadMoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.giokit_recycler_loadmore, parent, false)
        return LoadMoreViewHolder(view)
    }
}

class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)