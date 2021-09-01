package com.growingio.giokit.launch

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.growingio.giokit.R


/**
 * @author wanglikun
 * @date 2018/10/26
 */
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_gio_base, container, false)
        inflater.inflate(
            layoutId(),
            rootView!!.findViewById<View>(R.id.contentContainer) as FrameLayout,
            true
        )
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            if (view.context is Activity) {
                (view.context as Activity).window.decorView.requestLayout()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        initView()
        onViewCreated(view)
    }

    private fun initView() {
        val titleTv = findViewById<TextView>(R.id.title)
        titleTv.text = onGetTitle()
        val back = findViewById<ImageView>(R.id.close)
        back.setOnClickListener { requireActivity().finish() }
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