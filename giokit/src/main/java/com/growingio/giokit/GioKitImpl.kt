package com.growingio.giokit

import android.app.Application
import com.growingio.giokit.launch.GioKitLifecycleManager
import com.growingio.giokit.launch.db.DatabaseCreator
import com.growingio.giokit.launch.db.GioKitDatabase

/**
 * <p>
 *
 * @author cpacm 2021/8/12
 */
internal object GioKitImpl {

    lateinit var APPLICATION: Application
    lateinit var gioKitLifecycleManager: GioKitLifecycleManager

    fun install(app: Application) {
        APPLICATION = app

        initGioKitConfig()
        initGioTrack()

        DatabaseCreator.createDb(app){GioKitDatabase.initDb(app)}

        gioKitLifecycleManager = GioKitLifecycleManager(app)
    }

    //由插件注入配置信息
    private fun initGioKitConfig() {

    }

    //由插件注入埋点代码位置
    private fun initGioTrack() {

    }


}