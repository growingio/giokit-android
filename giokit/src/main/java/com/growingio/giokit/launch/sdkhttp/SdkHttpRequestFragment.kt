package com.growingio.giokit.launch.sdkhttp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.growingio.giokit.R
import com.growingio.giokit.launch.db.GioKitDbManager
import com.growingio.giokit.utils.CheckSdkStatusManager
import com.growingio.giokit.utils.MeasureUtils
import com.growingio.snappy.XORUtils
import org.iq80.snappy.Snappy
import java.lang.StringBuilder

/**
 * <p>
 *
 * @author cpacm 2021/11/1
 */
class SdkHttpRequestFragment : Fragment() {

    private var isCompress = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_giokit_sdkhttp_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getInt("id") ?: 0
        val http = GioKitDbManager.instance.getHttp(id)
        val requestSb = StringBuilder()
        requestSb.append("链接：").append(http.requestUrl).append("\n")
            .append("请求方式：").append(http.requestMethod).append("\n")
            .append("请求大小：").append(MeasureUtils.byte2FitMemorySize(http.requestSize, 2))
            .append("\n")
            .append("耗时：").append(MeasureUtils.millis2FitTimeSpan(http.httpCost))
        val detailTv = view.findViewById<TextView>(R.id.requestDetail)
        detailTv.text = requestSb.toString()

        val headerTv = view.findViewById<TextView>(R.id.requestHeader)
        headerTv.text = http.requestHeader

        val bodyTv = view.findViewById<TextView>(R.id.requestBody)
        bodyTv.text = http.requestBody

        val encode = view.findViewById<ImageView>(R.id.requestConvert)
        CheckSdkStatusManager.getInstance().ignoreViewClick(encode)
        encode.setOnClickListener {
            if (isCompress) {
                bodyTv.text = http.requestBody
                isCompress = false
            } else {
                isCompress = true
                val encodeData = Snappy.compress(http.requestBody.toByteArray())
                val compressedOut = XORUtils.encrypt(encodeData, (http.httpTime and 0xFF).toInt())
                bodyTv.text = String(compressedOut)
            }
        }
    }

    companion object {
        fun newInstance(id: Int): SdkHttpRequestFragment {
            val fragment = SdkHttpRequestFragment()
            val bundle = Bundle()
            bundle.putInt("id", id)
            fragment.arguments = bundle
            return fragment
        }
    }
}