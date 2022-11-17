package com.growingio.giokit.launch.sdkcrash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.growingio.giokit.R
import com.growingio.giokit.launch.db.GioKitDbManager
import com.growingio.giokit.utils.loadError

/**
 * <p>
 *
 * @author cpacm 2021/8/31
 */
class SdkErrorLogFragment : BottomSheetDialogFragment() {

    private var errorId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.GioSdkBottomSheetDialogTheme)

        val id = arguments?.getLong("id", 0) ?: 0
        if (id == 0L) {
            dismiss()
        } else {
            errorId = id
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_giokit_sdkerror_log, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val close = view.findViewById<ImageView>(R.id.close)
        close.setOnClickListener { dismiss() }

        val infoTv = view.findViewById<TextView>(R.id.info)
        val titleTv = view.findViewById<TextView>(R.id.title)

        val error = GioKitDbManager.instance.getBreadcrumb(errorId)
        infoTv.text = requireContext().loadError(error.extra)
        titleTv.text = error.message

    }

    companion object {
        fun newInstance(id: Long): SdkErrorLogFragment {
            val fragment = SdkErrorLogFragment()
            val bundle = Bundle()
            bundle.putLong("id", id)
            fragment.arguments = bundle
            return fragment
        }
    }
}