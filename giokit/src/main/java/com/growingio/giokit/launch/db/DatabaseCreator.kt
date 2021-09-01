package com.growingio.giokit.launch.db

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 *
 * <p>
 *
 * @author cpacm 2018/1/16
 */
object DatabaseCreator {

    @SuppressLint("CheckResult")
    fun createDb(context: Context, initDb: (context2: Context) -> Unit) {
        MainScope().launch(Dispatchers.IO) {
            initDb(context)
        }
    }
}