package com.growingio.giokit.launch.sdkdata

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.growingio.giokit.R
import com.growingio.giokit.launch.db.GioKitEventBean
import com.growingio.giokit.utils.CheckSdkStatusManager
import com.growingio.giokit.utils.MeasureUtils.getCurrentTime

/**
 * <p>
 *
 * @author cpacm 2021/8/31
 */
class SdkDataAdapter(val context: Context, val eventClick: (Int) -> Unit) :
    PagingDataAdapter<GioKitEventBean, RecyclerView.ViewHolder>(SdkEventDiffCallback()) {

    private var instantRangeStart = 0L
    private var instantRangeEnd = 0L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val view =
                LayoutInflater.from(context)
                    .inflate(R.layout.giokit_recycler_sdkcommon_date, parent, false)
            return DateViewHolder(view)

        } else {
            val view =
                LayoutInflater.from(context)
                    .inflate(R.layout.giokit_recycler_sdkdata_content, parent, false)
            return EventViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val event = getItem(position)
        if (event?.id == 0) return 1
        else return 0
    }

    fun setInstantRange(start: Long, end: Long) {
        instantRangeStart = start
        instantRangeEnd = end
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val event = getItem(position)!!
        if (holder is EventViewHolder) {
            holder.gsidTv.text = event.gsid.toString()
            holder.typeTv.text = event.type
            if (event.path.isNullOrEmpty()) {
                holder.pathTv.visibility = View.GONE
            } else {
                holder.pathTv.visibility = View.VISIBLE
                holder.pathTv.text = event.path
            }
            holder.statusTv.text = when (event.status) {
                0 -> {
                    holder.statusTv.setTextColor(
                        ContextCompat.getColor(
                            context,
                            android.R.color.black
                        )
                    )
                    "未发送"
                }

                -1 -> {
                    holder.statusTv.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.hover_primary
                        )
                    )
                    "过期"
                }

                else -> {
                    holder.statusTv.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.hover_accent
                        )
                    )
                    "已发送"
                }
            }
            holder.container.setBackgroundColor(
                ResourcesCompat.getColor(
                    context.resources,
                    android.R.color.white,
                    null
                )
            )
            if (instantRangeStart > 0L && instantRangeEnd > 0L) {
                if (event.gsid >= instantRangeStart && event.gsid <= instantRangeEnd) {
                    holder.container.setBackgroundColor(
                        ResourcesCompat.getColor(
                            context.resources,
                            R.color.hover_select_bg,
                            null
                        )
                    )
                }
            }
            holder.timeTv.text = getCurrentTime(event.time)
            holder.itemView.setOnClickListener {
                eventClick(event.id)
            }
        } else if (holder is DateViewHolder) {
            holder.dateTv.text = event.type
        }
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val container = itemView.findViewById<View>(R.id.container)
        val gsidTv = itemView.findViewById<TextView>(R.id.noTv)
        val typeTv = itemView.findViewById<TextView>(R.id.typeTv)
        val statusTv = itemView.findViewById<TextView>(R.id.statusTv)
        val timeTv = itemView.findViewById<TextView>(R.id.timeTv)
        val pathTv = itemView.findViewById<TextView>(R.id.pathTv)

        init {
            CheckSdkStatusManager.getInstance().ignoreViewClick(itemView)
        }

    }

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTv = itemView.findViewById<TextView>(R.id.date)
    }
}


class SdkEventDiffCallback : DiffUtil.ItemCallback<GioKitEventBean>() {
    override fun areItemsTheSame(oldItem: GioKitEventBean, newItem: GioKitEventBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GioKitEventBean, newItem: GioKitEventBean): Boolean {
        return oldItem.gsid == newItem.gsid && oldItem.status == newItem.status && oldItem.type == newItem.type
    }

}