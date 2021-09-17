package com.growingio.giokit.launch.sdkcode

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.growingio.giokit.R

/**
 * <p>
 *
 * @author cpacm 2021/8/30
 */
class SdkCodeAdapter(val context: Context) :
    RecyclerView.Adapter<SdkCodeAdapter.SdkCodeViewHolder>() {

    private val sdkCodelList = arrayListOf<SdkCode>()

    fun setData(list: List<SdkCode>) {
        sdkCodelList.clear()
        sdkCodelList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SdkCodeViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.giokit_recycler_sdkcode_content, parent, false)
        return SdkCodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: SdkCodeViewHolder, position: Int) {
        val sdkCode = sdkCodelList[position]
        holder.codeHeader.text = sdkCode.className
        val methodListCount = sdkCode.methodList.size
        val maxCount = holder.methodContainer.childCount

        if (methodListCount > maxCount) {
            for (index in maxCount until methodListCount) {
                val newView = TextView(context)
                newView.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.resources.getDimensionPixelSize(R.dimen.giokit_text_caption) *1.0f)
                holder.methodContainer.addView(newView, index)
            }
        } else if (maxCount > methodListCount) {
            holder.methodContainer.removeViews(methodListCount, maxCount - methodListCount)
        }

        sdkCode.methodList.forEachIndexed { index, s ->
            val methodView = holder.methodContainer.get(index) as TextView
            methodView.text = "┗ $s"
        }
    }


    override fun getItemCount(): Int {
        return sdkCodelList.size
    }

    inner class SdkCodeViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val codeHeader: TextView = itemView.findViewById(R.id.codeHeader)
        val methodContainer: LinearLayout = itemView.findViewById(R.id.methodContainer)
    }

}