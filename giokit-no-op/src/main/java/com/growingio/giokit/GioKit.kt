package com.growingio.giokit

import android.app.Application

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
    }

    class Builder(private val app: Application) {
        fun build() {
        }
    }
}