package com.growingio.giokit.hover.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.growingio.giokit.R
import com.growingio.giokit.launch.LaunchPage
import com.growingio.giokit.launch.UniversalActivity
import io.mattcarroll.hover.Content

/**
 * <p>
 *
 * @author cpacm 2021/8/26
 */
class GioSdkMenuContent(context: Context) : FrameLayout(context), Content, View.OnClickListener {


    init {
        LayoutInflater.from(context).inflate(R.layout.view_content_menu, this, true)

        val sdkInfoPage = findViewById(R.id.sdkInfoLayout) as View
        sdkInfoPage.setOnClickListener(this)

        val sdkCodeLayout = findViewById(R.id.sdkCodeLayout) as View
        sdkCodeLayout.setOnClickListener(this)

        val sdkDataLayout = findViewById(R.id.sdkDataLayout) as View
        sdkDataLayout.setOnClickListener(this)

    }

    override fun getView(): View {
        return this
    }

    override fun isFullscreen(): Boolean {
        return true
    }

    override fun onShown() {

    }

    override fun onHidden() {

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sdkInfoLayout -> {
                context.startActivity(Intent(context, UniversalActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(LaunchPage.LAUNCH_FRAGMENT_INDEX, LaunchPage.SDKINFO_PAGE)
                })
            }
            R.id.sdkCodeLayout -> {
                context.startActivity(Intent(context, UniversalActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(LaunchPage.LAUNCH_FRAGMENT_INDEX, LaunchPage.SDKCODE_PAGE)
                })
            }
            R.id.sdkDataLayout -> {
                context.startActivity(Intent(context, UniversalActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(LaunchPage.LAUNCH_FRAGMENT_INDEX, LaunchPage.SDKDATA_PAGE)
                })
            }
        }
    }
}