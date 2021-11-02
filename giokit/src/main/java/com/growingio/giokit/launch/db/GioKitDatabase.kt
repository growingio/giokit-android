package com.growingio.giokit.launch.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * <p>
 *
 * @author cpacm 2021/8/30
 */
@Database(
    entities = arrayOf(GioKitEventBean::class, GioKitHttpBean::class),
    version = 1,
    exportSchema = false
)
abstract class GioKitDatabase : RoomDatabase() {

    abstract fun getEventDao(): GioKitEventDao

    abstract fun getHttpDao(): GioKitHttpDao

    companion object {
        const val DATABASE_NAME = "giokit_db"
        lateinit var instance: GioKitDatabase

        fun initDb(context: Context) {
            instance = Room.databaseBuilder(context, GioKitDatabase::class.java, DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
        }
    }
}