package com.growingio.giokit.launch.sdkhttp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.growingio.giokit.GioKitImpl
import com.growingio.giokit.R
import com.growingio.giokit.utils.MeasureUtils
import com.growingio.giokit.utils.MeasureUtils.byte2FitMemorySize

/**
 * <p>
 *
 * @author cpacm 2021/10/28
 */
class SdkHttpHeaderAdapter : LoadStateAdapter<SdkHttpHeaderAdapter.HttpHeaderViewHolder>() {

    private var data: HttpStatusBean? = null

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): HttpHeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.giokit_recycler_sdkhttp_header, parent, false)
        return HttpHeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: HttpHeaderViewHolder, loadState: LoadState) {
        if (data != null) {
            val time =
                MeasureUtils.millis2FitTimeSpan(
                    System.currentTimeMillis() - GioKitImpl.launchTime,
                    4
                )
            holder.bind(
                time,
                data?.count ?: 0,
                byte2FitMemorySize(data?.size ?: 0L, 2) ?: "00.00B",
                data?.error ?: 0
            )
        } else {
            holder.bind("0秒", 0, "00.00B", 0)
        }
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return true
    }


    fun setData(data: HttpStatusBean) {
        this.data = data
        notifyDataSetChanged()
    }

    class HttpHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val runningTimeTv = itemView.findViewById<TextView>(R.id.runningTime)
        private val capCountTv = itemView.findViewById<TextView>(R.id.capCount)
        private val capSizeTv = itemView.findViewById<TextView>(R.id.capSize)
        private val capErrorTv = itemView.findViewById<TextView>(R.id.capError)

        fun bind(time: String, count: Int, size: String, error: Int) {
            runningTimeTv.text = time
            capCountTv.text = "$count"
            capSizeTv.text = size
            capErrorTv.text = "$error"
        }

    }

}

data class HttpStatusBean(
    var tick: Int,
    var count: Int,
    var size: Long,
    var error: Int,
    var update: Boolean = false
)