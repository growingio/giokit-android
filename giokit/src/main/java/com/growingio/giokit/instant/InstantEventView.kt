package com.growingio.giokit.instant

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.growingio.giokit.R
import com.growingio.giokit.circle.FloatViewContainer
import com.growingio.giokit.circle.FloatWindowManager
import com.growingio.giokit.instant.InstantEventCache.INSTANT_DISPLAY_MAX_COUNT
import com.growingio.giokit.launch.LaunchPage
import com.growingio.giokit.launch.UniversalActivity
import com.growingio.giokit.utils.CheckSdkStatusManager
import java.lang.StringBuilder

/**
 * <p>
 *
 * @author cpacm 2022/8/11
 */
class InstantEventView(context: Context) : FloatViewContainer(context) {

    private val instantEventList: RecyclerView
    private val instantAdapter: InstantDataAdapter

    init {
        LayoutInflater.from(getContext()).inflate(R.layout.giokit_view_instant_event_container, this, true)
        instantEventList = findViewById(R.id.view_instant_event_list)
        instantEventList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        instantAdapter = InstantDataAdapter(context)
        instantEventList.adapter = instantAdapter

        InstantEventCache.addInstantObserver(instantAdapter)
    }

    fun isShow(): Boolean {
        return parent != null && visibility == VISIBLE
    }

    fun hide() {
        if (parent != null) {
            visibility = GONE
        }
    }

    fun show() {
        if (parent == null) {
            val flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            val maskParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                -1, flags, PixelFormat.TRANSLUCENT
            )
            maskParams.gravity = Gravity.BOTTOM or Gravity.END
            maskParams.title = "InstantMonitorWindow:" + context.packageName
            FloatWindowManager.getInstance().addView(this, maskParams)
        } else {
            visibility = VISIBLE
            bringToFront()
        }
    }

    fun remove() {
        FloatWindowManager.getInstance().removeView(this)
        InstantEventCache.removeInstantObserver(instantAdapter)
    }

    class InstantDataAdapter internal constructor(val context: Context) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), InstantEventCache.InstantDataObserver {

        private val dataList: ArrayList<InstantEventCache.InstantData> = arrayListOf()

        init {
            InstantEventCache.addInstantObserver(this)
        }

        override fun dispatchInstantData(data: InstantEventCache.InstantData) {
            if (dataList.size >= INSTANT_DISPLAY_MAX_COUNT) {
                notifyItemRemoved(dataList.size - 1)
                dataList.removeLast()
            }
            dataList.add(0, data)
            notifyItemInserted(0)
        }

        override fun removeInstantData(data: InstantEventCache.InstantData) {
            if (dataList.contains(data)) {
                val index = dataList.indexOf(data)
                notifyItemRemoved(index)
                data.isVisible = false
                dataList.remove(data)
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            if (viewType == 1) {
                val view = LayoutInflater.from(context).inflate(R.layout.giokit_recycler_instant_single, parent, false)
                return InstantSingleViewHolder(view)
            } else {
                val view = LayoutInflater.from(context).inflate(R.layout.giokit_recycler_instant_multi, parent, false)
                return InstantBundleViewHolder(view)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is InstantSingleViewHolder) {
                val instantData = dataList.get(position)
                val data = instantData.events.first()
                holder.textAliasTv.setText(CheckSdkStatusManager.getInstance().getEventAlphaBet(data.type))
                holder.gesidTv.setText(data.gsid.toString())
                holder.typeTv.setText(data.type.uppercase())
                holder.descTv.setText(CheckSdkStatusManager.getInstance().getEventDesc(data.type, data.data))
                holder.setVisible(instantData.isVisible)
                holder.click(context, data.gsid, data.gsid)
            } else if (holder is InstantBundleViewHolder) {
                val instantData = dataList.get(position)
                val sb = StringBuilder()
                sb.append(instantData.events.first().gsid).append("\n~\n").append(instantData.events.last().gsid)
                holder.idsTv.setText(sb.toString())
                val data = instantData.events.joinToString(separator = "\n* ", prefix = "* ") {
                    it.type
                }
                holder.list.setText(data)
                holder.setVisible(instantData.isVisible)
                holder.click(context, instantData.events.first().gsid, instantData.events.last().gsid)
            }
        }

        override fun getItemViewType(position: Int): Int {
            val item = dataList.get(position)
            if (item.events.size <= 1) {
                return 1
            }
            return 0
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        class InstantSingleViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textAliasTv = itemView.findViewById<TextView>(R.id.typeAlias)
            val gesidTv = itemView.findViewById<TextView>(R.id.gesid)
            val typeTv = itemView.findViewById<TextView>(R.id.type)
            val descTv = itemView.findViewById<TextView>(R.id.desc)

            fun setVisible(visible: Boolean) {
                itemView.visibility = if (visible) View.VISIBLE else View.GONE
            }

            fun click(context: Context, start: Long, end: Long) {
                itemView.setOnClickListener {
                    context.startActivity(Intent(context, UniversalActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra(LaunchPage.LAUNCH_FRAGMENT_INDEX, LaunchPage.SDKDATA_PAGE)
                        putExtra(LaunchPage.SDKDATA_PAGE_RANGE_START, start)
                        putExtra(LaunchPage.SDKDATA_PAGE_RANGE_END, end)
                    })
                }
            }
        }

        class InstantBundleViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
            val list = itemView.findViewById<TextView>(R.id.eventList)
            val idsTv = itemView.findViewById<TextView>(R.id.ids)

            fun setVisible(visible: Boolean) {
                itemView.visibility = if (visible) View.VISIBLE else View.GONE
            }

            fun click(context: Context, start: Long, end: Long) {
                itemView.setOnClickListener {
                    context.startActivity(Intent(context, UniversalActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra(LaunchPage.LAUNCH_FRAGMENT_INDEX, LaunchPage.SDKDATA_PAGE)
                        putExtra(LaunchPage.SDKDATA_PAGE_RANGE_START, start)
                        putExtra(LaunchPage.SDKDATA_PAGE_RANGE_END, end)
                    })
                }
            }
        }

    }

    companion object {
        const val TAG = "InstantEventView"
    }
}