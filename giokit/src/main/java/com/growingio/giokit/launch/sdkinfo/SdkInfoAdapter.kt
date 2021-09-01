package com.growingio.giokit.launch.sdkinfo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.growingio.giokit.R

/**
 * <p>
 *
 * @author cpacm 2021/8/26
 */
class SdkInfoAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val sdkInfolList = arrayListOf<SdkInfo>()

    fun setData(list: List<SdkInfo>) {
        sdkInfolList.clear()
        sdkInfolList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.recycler_sdkinfo_header, parent, false)
            return SdkHeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.recycler_sdkinfo_content, parent, false)
            return SdkInfoViewHolder(view)
        }

    }

    override fun getItemViewType(position: Int): Int {
        val sdkInfo = sdkInfolList[position]
        return if (sdkInfo.isHeader) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val sdkInfo = sdkInfolList[position]
        if (holder is SdkHeaderViewHolder) {
            holder.headerTv.text = sdkInfo.title
        } else if (holder is SdkInfoViewHolder) {
            holder.titleTv.text = sdkInfo.title
            holder.descTv.text = sdkInfo.desc
        }
    }

    override fun getItemCount(): Int {
        return sdkInfolList.size
    }

    internal class SdkHeaderViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerTv: TextView = itemView.findViewById(R.id.header)
    }

    internal class SdkInfoViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTv: TextView = itemView.findViewById(R.id.titleTv)
        val descTv: TextView = itemView.findViewById(R.id.resultTv)

    }
}