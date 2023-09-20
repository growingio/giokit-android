package com.growingio.giokit.launch.sdkdata

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.growingio.giokit.GioKitImpl
import com.growingio.giokit.R
import com.growingio.giokit.hook.GioPluginConfig
import com.growingio.giokit.utils.MeasureUtils
import com.growingio.giokit.utils.MeasureUtils.byte2FitMemorySize

/**
 * <p>
 *
 * @author cpacm 2021/10/28
 */
class SdkHttpHeaderAdapter(val searchListener: SdkSearchView.OnSearchListener) :
    RecyclerView.Adapter<SdkHttpHeaderAdapter.DataHeaderViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.giokit_recycler_sdkdata_header, parent, false)
        return DataHeaderViewHolder(view)
    }


    override fun onBindViewHolder(holder: DataHeaderViewHolder, position: Int) {
        holder.searchView.setEventData(
            arrayListOf(
                "PAGE", "CUSTOM",
                "VISIT", "VIEW_CLICK",
                "APP_CLOSED", "VIEW_CHANGE",
                "FORM_SUBMIT", "PAGE_ATTRIBUTES",
                "CONVERSION_VARIABLES", "LOGIN_USER_ATTRIBUTES",
                "VISITOR_ATTRIBUTES"
            )
        )
    }


    override fun getItemCount(): Int {
        return 1
    }

    inner class DataHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val searchView = itemView.findViewById<SdkSearchView>(R.id.searchView)

        init {
            searchView.searchListener = searchListener
        }
    }
}