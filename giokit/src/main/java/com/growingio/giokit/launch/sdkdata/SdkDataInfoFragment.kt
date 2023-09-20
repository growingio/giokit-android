package com.growingio.giokit.launch.sdkdata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.growingio.giokit.R
import com.growingio.giokit.launch.db.GioKitDbManager
import com.growingio.giokit.utils.CheckSdkStatusManager
import java.lang.StringBuilder

/**
 * <p>
 *
 * @author cpacm 2021/8/31
 */
class SdkDataInfoFragment : BottomSheetDialogFragment() {

    private var eventId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.GioSdkBottomSheetDialogTheme)

        val id = arguments?.getInt("id", 0) ?: 0
        if (id == 0) {
            dismiss()
        } else {
            eventId = id
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_giokit_sdkdata_info, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val close = view.findViewById<ImageView>(R.id.close)
        CheckSdkStatusManager.getInstance().ignoreViewClick(close)
        close.setOnClickListener { dismiss() }

        val infoTv = view.findViewById<TextView>(R.id.info)
        val titleTv = view.findViewById<TextView>(R.id.title)

        val event = GioKitDbManager.instance.getEvent(eventId)
        infoTv.text = beautyJson(event.data)
        titleTv.text = event.type

    }

    fun beautyJson(json: String): String {
        var tabCount = 0
        var quote = 0
        val sb = StringBuilder(replaceUnicode(json))
        var i = 0

        while (i < sb.length) {
            val c = sb[i]
            if (c == '"') {
                quote += 1
            }
            if (quote % 2 == 1) {
                i++
            } else if (c == '{') {
                tabCount += 1
                i = warpLine(sb, tabCount, i)
            } else if (c == '}') {
                tabCount -= 1
                i = warpLine(sb, tabCount, i - 1) + 1
            } else if (c == ',') {
                i = warpLine(sb, tabCount, i)
            } else {
                i++
            }
        }
        return sb.toString()
    }

    fun replaceUnicode(json: String): String {
        return json.replace("\\u003e", ">")
            .replace("\\u003c", "<")
            .replace("\\u0025", "%")
            .replace("\\u0024", "$")
            .replace("\\u003d", "=")
            .replace("\\u0026", "&")
            .replace("\\u0040", "@")
            .replace("\\u002a", "*")
            .replace("""\/""", "/")
    }

    fun warpLine(sb: StringBuilder, tab: Int, i: Int): Int {
        var pos = i + 1
        sb.insert(pos, "\n")
        pos += "\n".length
        for (j in 0 until tab) {
            sb.insert(pos, "\t\t")
            pos += "\t\t".length
        }
        return pos
    }

    companion object {
        fun newInstance(id: Int): SdkDataInfoFragment {
            val fragment = SdkDataInfoFragment()
            val bundle = Bundle()
            bundle.putInt("id", id)
            fragment.arguments = bundle
            return fragment
        }
    }
}