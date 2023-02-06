package com.growingio.giokit.launch

import android.app.Activity
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.growingio.giokit.R


abstract class BaseFragment : Fragment() {
    @JvmField
    val TAG = this.javaClass.simpleName

    /**
     * @return 资源文件
     */
    @LayoutRes
    abstract fun layoutId(): Int


    fun <T : View> findViewById(@IdRes id: Int): T {
        return requireView().findViewById(id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_giokit_base, container, false)
        inflater.inflate(
            layoutId(), rootView!!.findViewById<View>(R.id.contentContainer) as FrameLayout, true
        )
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            if (view.context is Activity) {
                (view.context as Activity).window.decorView.requestLayout()
            }
        } catch (ignored: Exception) {
        }

        initView()
        onViewCreated(view)
    }

    private fun initView() {
        val title = onGetTitle()
        setTitle(title)
        val close = findViewById<View>(R.id.close)
        close.setOnClickListener {
            finish()
        }
    }

    fun setTitle(title: String?) {
        try {
            if (title == null || title.isEmpty()) return
            val titleTv = findViewById<TextView>(R.id.title)
            if (title.length > 8) {
                titleTv.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.giokit_text_subtitle)
                )
            }
            titleTv.text = title
        } catch (ignored: IllegalStateException) {
        }
    }


    abstract fun onViewCreated(view: View?)

    abstract fun onGetTitle(): String

    open fun onBackPressed(): Boolean {
        return false
    }

    @JvmOverloads
    fun showContent(fragmentClass: Class<out BaseFragment>, bundle: Bundle? = null) {
        val activity = activity as UniversalActivity?
        activity?.showContent(fragmentClass, bundle)
    }

    fun finish() {
        val activity = activity as UniversalActivity?
        activity?.doBack(this)
    }
}