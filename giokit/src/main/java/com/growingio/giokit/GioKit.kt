package com.growingio.giokit

import android.app.Application
import com.growingio.giokit.hover.GioKitHoverManager

/**
 * <p>
 *     对外 API 接口
 * @author cpacm 2021/8/11
 */
public class GioKit private constructor() {

    companion object {
        const val TAG = "GIOKIT"

        fun getGioKitHoverManager(): GioKitHoverManager {
            return GioKitImpl.gioKitHoverManager
        }
    }


    class Builder(private val app: Application) {
        fun build() {
            GioKitImpl.install(app)
        }
    }
}