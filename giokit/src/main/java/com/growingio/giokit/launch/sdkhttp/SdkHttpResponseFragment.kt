package com.growingio.giokit.launch.sdkhttp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.growingio.giokit.R
import com.growingio.giokit.launch.db.GioKitDbManager
import com.growingio.giokit.utils.MeasureUtils
import java.lang.StringBuilder

/**
 * <p>
 *
 * @author cpacm 2021/11/1
 */
class SdkHttpResponseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_giokit_sdkhttp_response, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getInt("id") ?: 0
        val http = GioKitDbManager.instance.getHttp(id)
        val requestSb = StringBuilder()
        requestSb.append("链接：").append(http.responseUrl).append("\n")
            .append("返回码：").append(http.responseCode).append("\n")
            .append("返回信息：")
            .append(if (http.responseMessage.isNullOrEmpty()) MeasureUtils.getHttpMessage(http.responseCode) else http.responseMessage)
        val detailTv = view.findViewById<TextView>(R.id.responseDetail)
        detailTv.text = requestSb.toString()

        val headerTv = view.findViewById<TextView>(R.id.responseHeader)
        headerTv.text = http.responseHeader
    }

    companion object {
        fun newInstance(id: Int): SdkHttpResponseFragment {
            val fragment = SdkHttpResponseFragment()
            val bundle = Bundle()
            bundle.putInt("id", id)
            fragment.arguments = bundle
            return fragment
        }
    }
}