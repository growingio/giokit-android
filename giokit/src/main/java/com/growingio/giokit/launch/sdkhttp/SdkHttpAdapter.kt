package com.growingio.giokit.launch.sdkhttp

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.growingio.giokit.R
import com.growingio.giokit.launch.db.GioKitHttpBean
import com.growingio.giokit.utils.MeasureUtils
import com.growingio.giokit.utils.MeasureUtils.getDefaultTime
import com.growingio.giokit.utils.MeasureUtils.getHttpMessage
import com.growingio.giokit.utils.MeasureUtils.isHttpSuccessful
import com.growingio.giokit.utils.MeasureUtils.millis2FitTimeSpan

/**
 * <p>
 *
 * @author cpacm 2021/8/31
 */
class SdkHttpAdapter(val httpClick: (Int) -> Unit) :
    PagingDataAdapter<GioKitHttpBean, RecyclerView.ViewHolder>(SdkHttpDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.giokit_recycler_sdkhttp_date, parent, false)
            return DateViewHolder(view)

        } else {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.giokit_recycler_sdkhttp_content, parent, false)
            return HttpViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val event = getItem(position)
        if (event?.id == 0) return 1
        else return 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val http = getItem(position)
        if (holder is HttpViewHolder) {
            if (http == null) return
            val isSuccess = isHttpSuccessful(http.responseCode)
            holder.httpCode.setTextColor(
                ContextCompat.getColor(
                    holder.httpCode.context,
                    if (isSuccess) R.color.giokit_http_safe else R.color.giokit_http_error
                )
            )
            holder.httpCode.text = http.responseCode.toString()

            holder.httpMessage.text = if (TextUtils.isEmpty(http.responseMessage)) {
                getHttpMessage(http.responseCode)
            } else http.responseMessage

            holder.httpUrl.text = http.requestUrl
            holder.httpDate.text = getDefaultTime(http.httpTime)
            val costTime = holder.httpCost.context.getString(
                R.string.giokit_http_cost,
                millis2FitTimeSpan(http.httpCost)
            )
            holder.httpCost.text = costTime
            holder.httpMethod.text = http.requestMethod
            holder.httpSize.text = MeasureUtils.byte2FitMemorySize(http.requestSize, 2)
            holder.itemView.setOnClickListener { httpClick.invoke(http.id) }
        } else if (holder is DateViewHolder) {
            holder.dateTv.text = http?.requestMethod
        }
    }

    inner class HttpViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val httpCode = itemView.findViewById<TextView>(R.id.httpCode)
        val httpMessage = itemView.findViewById<TextView>(R.id.httpMessage)
        val httpUrl = itemView.findViewById<TextView>(R.id.httpUrl)
        val httpDate = itemView.findViewById<TextView>(R.id.httpDate)
        val httpCost = itemView.findViewById<TextView>(R.id.httpCost)
        val httpMethod = itemView.findViewById<TextView>(R.id.httpMethod)
        val httpSize = itemView.findViewById<TextView>(R.id.httpSize)

    }

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTv = itemView.findViewById<TextView>(R.id.date)
    }
}


class SdkHttpDiffCallback : DiffUtil.ItemCallback<GioKitHttpBean>() {
    override fun areItemsTheSame(oldItem: GioKitHttpBean, newItem: GioKitHttpBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GioKitHttpBean, newItem: GioKitHttpBean): Boolean {
        return oldItem.id == newItem.id
    }

}