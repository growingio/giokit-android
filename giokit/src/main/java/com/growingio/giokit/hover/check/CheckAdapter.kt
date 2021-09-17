package com.growingio.giokit.hover.check

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.growingio.giokit.R

/**
 * <p>
 *
 * @author cpacm 2021/8/16
 */
class CheckAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data = arrayListOf<CheckItem>()

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }

    fun updateData(item: CheckItem) {
        val index = item.index
        if (index < 0 || index >= data.size) return
        data.set(index, item)
        notifyItemChanged(index)
    }

    fun addData(item: CheckItem) {
        val index = item.index
        if (index != data.size) return
        data.add(item)
        notifyItemInserted(index)
    }

    fun setData(newData: List<CheckItem>) {
        val oldData = arrayListOf<CheckItem>()
        for (item in data) {
            oldData.add(item)
        }
        data.clear()
        data.addAll(newData)

        val diffResult = DiffUtil.calculateDiff(CheckDiffCallback(oldData, data))
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val view =
                LayoutInflater.from(context).inflate(R.layout.giokit_recycler_check_loading, parent, false)
            return LoadingViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(context).inflate(R.layout.giokit_recycler_check_content, parent, false)
            return ContentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data.get(position)
        if (holder is ContentViewHolder) {
            holder.titleTv.text = item.title
            holder.resultTv.text = item.content
            if (item.isError) {
                holder.resultTv.setTextColor(ContextCompat.getColor(context, R.color.hover_dark))
            } else {
                holder.resultTv.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            }
        } else if (holder is LoadingViewHolder) {
            holder.loadingTv.text = item.loading
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = data.get(position)
        if (item.checked) return 1
        return 0
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loadingView: SpinKitView = itemView.findViewById(R.id.loadingView)
        val loadingTv: TextView = itemView.findViewById(R.id.loadingTv)
    }

    class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTv: TextView = itemView.findViewById(R.id.titleTv)
        val resultTv: TextView = itemView.findViewById(R.id.resultTv)

    }
}