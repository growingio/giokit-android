package com.growingio.giokit.launch.sdkhttp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.growingio.giokit.R

/**
 * <p>
 *
 * @author cpacm 2021/8/31
 */
class SdkHttpDetailFragment : BottomSheetDialogFragment() {

    private var httpId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.GioSdkBottomSheetDialogTheme)


        val id = arguments?.getInt("id", 0) ?: 0
        if (id == 0) {
            dismiss()
        } else {
            httpId = id
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_giokit_sdkhttp_detail, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val close = view.findViewById<ImageView>(R.id.close)
        close.setOnClickListener { dismiss() }

        val adapter = SdkHttpFragmentAdapter(requireActivity(), httpId)
        val tabMenu = view.findViewById<TabLayout>(R.id.requestTab)
        val viewPager2 = view.findViewById<ViewPager2>(R.id.viewPager2)
        viewPager2.adapter = adapter

        viewPager2.getRecyclerView()?.isNestedScrollingEnabled = false
        viewPager2.getRecyclerView()?.overScrollMode = View.OVER_SCROLL_NEVER
        TabLayoutMediator(tabMenu, viewPager2, true) { tab, position ->
            tab.text = adapter.getItemTitle(position)
        }.attach()

    }

    companion object {
        fun newInstance(id: Int): SdkHttpDetailFragment {
            val fragment = SdkHttpDetailFragment()
            val bundle = Bundle()
            bundle.putInt("id", id)
            fragment.arguments = bundle
            return fragment
        }
    }
}

// fix viewpager2 not scroll in bottomSheetDialogFragment
// https://stackoverflow.com/questions/67749500/how-to-enable-dragging-on-viewpager2-inside-bottomsheetdialogfragment
fun ViewPager2.getRecyclerView(): RecyclerView? {
    try {
        val field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        field.isAccessible = true
        return field.get(this) as RecyclerView
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }
    return null
}

class SdkHttpFragmentAdapter(val context: FragmentActivity, val httpId: Int) :
    FragmentStateAdapter(context) {

    private val titles: Array<Int> =
        arrayOf(R.string.giokit_http_request, R.string.giokit_http_response)

    fun getItemTitle(position: Int): String {
        return context.getString(titles[position])
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) SdkHttpRequestFragment.newInstance(httpId)
        else SdkHttpResponseFragment.newInstance(httpId)
    }

}