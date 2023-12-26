package com.growingio.giokit.launch.sdkdata

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.growingio.giokit.R
import com.growingio.giokit.utils.CheckSdkStatusManager

/**
 *
 * 搜索類型選擇
 */
class SdkSearchEventPopupWindow(val context: Context) : PopupWindow(context) {

    private val parentView: View
    private var recyclerView: RecyclerView
    private val eventAdapter: EventAdapter by lazy { EventAdapter { onEventClick(it) } }

    private var onSearchTypeListener: OnSearchEventListener? = null

    init {

        parentView =
            LayoutInflater.from(context).inflate(R.layout.giokit_pop_data_search_type, null)

        contentView = parentView
        width = ViewGroup.LayoutParams.WRAP_CONTENT

//        if (context is Activity) {
//            val wH = DeviceUtils.getRealHeightPixels(context)
//            height = Math.min((wH * 0.4).toInt(), DeviceUtils.dp2Px(context, 40f * 6))
//        } else {
//            height = ViewGroup.LayoutParams.WRAP_CONTENT
//        }
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        setBackgroundDrawable(ColorDrawable(0))
        isOutsideTouchable = true
        isFocusable = true

        recyclerView = parentView.findViewById(R.id.eventList)
        initView()
    }

    fun setEventData(data: List<String>) {
        eventAdapter.setEventData(data)
    }

    private fun initView() {
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = eventAdapter
    }

    private fun onEventClick(event: String) {
        onSearchTypeListener?.onEventClick(event)
        dismiss()
    }


    fun showAsDropDown(anchor: View, listener: OnSearchEventListener) {
        this.onSearchTypeListener = listener
        super.showAsDropDown(anchor, 0, 0)
    }


    interface OnSearchEventListener {
        fun onEventClick(event: String)
    }
}

class EventAdapter(val eventClick: (String) -> Unit) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private var eventData = arrayListOf<String>()

    fun setEventData(data: List<String>) {
        this.eventData.clear()
        this.eventData.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.giokit_recycler_sdkdata_event, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val eventName = eventData[position]
        if (eventName.length > 10) {
            holder.eventTv.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                holder.eventTv.context.resources.getDimension(R.dimen.giokit_text_caption)
            )
            holder.eventTv.text = eventName.replace("_", " ")
        } else {
            holder.eventTv.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                holder.eventTv.context.resources.getDimension(R.dimen.giokit_text_body)
            )
            holder.eventTv.text = eventName
        }
        holder.itemView.setOnClickListener { eventClick(eventData[position]) }
    }

    override fun getItemCount(): Int {
        return eventData.size
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventTv: TextView = itemView.findViewById(R.id.event)
        init {
            CheckSdkStatusManager.getInstance().ignoreViewClick(itemView)
        }
    }
}
