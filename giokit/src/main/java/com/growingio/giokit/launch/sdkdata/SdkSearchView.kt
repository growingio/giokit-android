package com.growingio.giokit.launch.sdkdata

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import com.growingio.giokit.R

class SdkSearchView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defAttrStyle: Int = 0
) : FrameLayout(context, attributeSet, defAttrStyle) {
    private var searchEt: EditText? = null
    private var clearIv: ImageView? = null
    private var typeTv: TextView? = null
    var searchListener: OnSearchListener? = null
    private var hasShown: Boolean = false
    private var type: Int = 0
    private val menu: SdkSearchEventPopupWindow

    init {
        initSearchView(context)
        menu = SdkSearchEventPopupWindow(context)
    }

    fun setEventData(data: List<String>) {
        menu.setEventData(data)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSearchView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.giokit_sdkdata_searchview, this, true)
        searchEt = findViewById(R.id.search_et)
        clearIv = findViewById(R.id.search_close)
        clearIv!!.setOnClickListener { searchEt!!.setText("") }

        typeTv = findViewById(R.id.search_type)

        typeTv!!.setOnClickListener {
            menu.showAsDropDown(typeTv!!, object : SdkSearchEventPopupWindow.OnSearchEventListener {
                override fun onEventClick(event: String) {
                    this@SdkSearchView.searchEt?.setText(event)
                    searchListener?.onSearch(event)
                }
            })
        }

        searchEt!!.setOnEditorActionListener { v, actionId, event ->
            if ((actionId == EditorInfo.IME_ACTION_NONE || actionId == EditorInfo.IME_ACTION_SEARCH)) {
                if (TextUtils.isEmpty(v.text)) {
                    return@setOnEditorActionListener true
                }
                searchListener?.onSearch(v.text.toString())
                closeSearch()
                false
            } else true
        }
        searchEt!!.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP && !hasShown) {
                openSearch()
            }
            false
        }

        searchEt!!.requestFocus()
        openSearch()
    }

    fun openSearch() {
        hasShown = true
        showKeyboard(searchEt)
        clearIv!!.visibility = View.VISIBLE
    }

    fun setText(str: String) {
        searchEt?.setText(str)
    }

    fun setType(type: Int) {
        this.type = type
    }

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @SuppressLint("ObsoleteSdkInt")
    fun showKeyboard(view: View?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && view!!.hasFocus()) {
            view.clearFocus()
        }
        view!!.requestFocus()
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0)
    }

    private fun closeSearch() {
        hasShown = false
        searchEt!!.clearFocus()
        hideKeyboard(searchEt!!)
        clearIv!!.visibility = View.INVISIBLE
    }

    interface OnSearchListener {
        fun onSearch(keyword: String)
    }
}
