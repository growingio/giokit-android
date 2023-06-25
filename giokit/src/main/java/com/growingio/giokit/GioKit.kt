package com.growingio.giokit

import android.app.Activity
import android.app.Application
import android.util.Log
import com.growingio.android.gmonitor.GMonitorOption

/**
 * <p>
 *     对外 API 接口
 * @author cpacm 2021/8/11
 */
public class GioKit private constructor() {

    companion object {
        const val TAG = "GIOKIT"

        @JvmStatic
        fun with(app: Application): Builder {
            return Builder(app)
        }

        @JvmStatic
        fun attach(activity: Activity) {
            if (!GioKitImpl.inited) {
                Log.e(TAG, "GioKit not init")
            }
            GioKitImpl.gioKitHoverManager.attach(activity)
        }

        @JvmStatic
        fun detach(activity: Activity) {
            if (!GioKitImpl.inited) {
                Log.e(TAG, "GioKit not init")
            }
            GioKitImpl.gioKitHoverManager.detach(activity)
        }
    }


    class Builder(private val app: Application) {

        private var attach = true

        fun attach(attach: Boolean): Builder {
            this.attach = attach
            return this
        }

        fun build() {
            val gioKitOption = GioKitOption(attach)
            GioKitImpl.install(app, gioKitOption)
        }
    }
}