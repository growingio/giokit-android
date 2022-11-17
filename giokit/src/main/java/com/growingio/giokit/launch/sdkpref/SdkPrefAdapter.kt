package com.growingio.giokit.launch.sdkpref

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.growingio.giokit.R
import com.growingio.giokit.launch.db.GioKitBreadCrumb
import com.growingio.giokit.utils.MeasureUtils.getCurrentTime

/**
 * <p>
 *
 * @author cpacm 2022/10/24
 */
class SdkPrefAdapter :
    PagingDataAdapter<GioKitBreadCrumb, RecyclerView.ViewHolder>(SdkPrefDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.giokit_recycler_sdkcommon_date, parent, false)
            return DateViewHolder(view)

        } else {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.giokit_recycler_sdkpref_content, parent, false)
            return PrefViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val event = getItem(position)
        if (event?.id == 0L) return 1
        else return 0
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val pref = getItem(position)
        if (holder is PrefViewHolder) {
            if (pref == null) return
            holder.prefTime.text = getCurrentTime(pref.time)
            holder.prefContent.text = pref.content
            holder.prefName.text = pref.message
            holder.prefDuration.text = pref.duration.toString() + " ms"
            holder.prefType.text = pref.category

        } else if (holder is DateViewHolder) {
            holder.dateTv.text = pref?.extra
        }
    }

    inner class PrefViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prefContent = itemView.findViewById<TextView>(R.id.prefContent)
        val prefName = itemView.findViewById<TextView>(R.id.prefName)
        val prefDuration = itemView.findViewById<TextView>(R.id.prefDuration)
        val prefTime = itemView.findViewById<TextView>(R.id.prefTime)
        val prefType = itemView.findViewById<TextView>(R.id.prefType)
    }

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTv = itemView.findViewById<TextView>(R.id.date)
    }
}


class SdkPrefDiffCallback : DiffUtil.ItemCallback<GioKitBreadCrumb>() {
    override fun areItemsTheSame(oldItem: GioKitBreadCrumb, newItem: GioKitBreadCrumb): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GioKitBreadCrumb, newItem: GioKitBreadCrumb): Boolean {
        return oldItem.id == newItem.id
    }

}