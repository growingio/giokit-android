package com.growingio.giokit.launch.sdkcrash

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.growingio.giokit.R
import com.growingio.giokit.launch.db.GioKitBreadCrumb
import com.growingio.giokit.utils.CheckSdkStatusManager
import com.growingio.giokit.utils.MeasureUtils.getCurrentTime

/**
 * <p>
 *
 * @author cpacm 2022/10/24
 */
class SdkErrorAdapter(val errorClick: (Long) -> Unit) :
    PagingDataAdapter<GioKitBreadCrumb, RecyclerView.ViewHolder>(SdkErrorDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.giokit_recycler_sdkcommon_date, parent, false)
            return DateViewHolder(view)

        } else {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.giokit_recycler_sdkerror_content, parent, false)
            return ErrorViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val event = getItem(position)
        if (event?.id == 0L) return 1
        else return 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val error = getItem(position)
        if (holder is ErrorViewHolder) {
            if (error == null) return
            holder.errorDate.text = getCurrentTime(error.time)
            holder.errorType.text = error.category
            holder.errorMessage.text = error.message
            holder.errorAt.text = error.content

            holder.itemView.setOnClickListener { errorClick.invoke(error.id) }
        } else if (holder is DateViewHolder) {
            holder.dateTv.text = error?.extra
        }
    }

    inner class ErrorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val errorDate = itemView.findViewById<TextView>(R.id.errorDate)
        val errorType = itemView.findViewById<TextView>(R.id.errorType)
        val errorMessage = itemView.findViewById<TextView>(R.id.errorMessage)
        val errorAt = itemView.findViewById<TextView>(R.id.errorAt)
        init {
            CheckSdkStatusManager.getInstance().ignoreViewClick(itemView)
        }
    }

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTv = itemView.findViewById<TextView>(R.id.date)
    }
}


class SdkErrorDiffCallback : DiffUtil.ItemCallback<GioKitBreadCrumb>() {
    override fun areItemsTheSame(oldItem: GioKitBreadCrumb, newItem: GioKitBreadCrumb): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GioKitBreadCrumb, newItem: GioKitBreadCrumb): Boolean {
        return oldItem.id == newItem.id
    }

}