package com.growingio.giokit.launch.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * <p>
 *
 * @author cpacm 2021/8/30
 */
@Database(
    entities = [GioKitEventBean::class, GioKitHttpBean::class, GioKitBreadCrumb::class],
    version = 3,
    exportSchema = false
)
abstract class GioKitDatabase : RoomDatabase() {

    abstract fun getEventDao(): GioKitEventDao

    abstract fun getHttpDao(): GioKitHttpDao

    abstract fun getBreadcrumbDao(): GioKitBreadCrumbDao

    companion object {
        const val DATABASE_NAME = "giokit_db"
        lateinit var instance: GioKitDatabase

        fun initDb(context: Context) {
            instance = Room.databaseBuilder(context, GioKitDatabase::class.java, DATABASE_NAME)
                .allowMainThreadQueries()
                .addMigrations(Migration1_2)
                .addMigrations(Migration2_3)
                .build()
        }

        private val Migration1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS `https`")
                database.execSQL("CREATE TABLE IF NOT EXISTS `https` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `requestUrl` TEXT, `requestMethod` TEXT, `requestHeader` TEXT, `requestBody` TEXT, `requestSize` INTEGER NOT NULL, `responseUrl` TEXT, `responseCode` INTEGER NOT NULL, `responseHeader` TEXT, `responseBody` TEXT, `responseMessage` TEXT, `responseSize` INTEGER NOT NULL, `httpCost` INTEGER NOT NULL, `httpTime` INTEGER NOT NULL)")
            }

        }

        private val Migration2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS `breadcrumb`")
                database.execSQL("CREATE TABLE IF NOT EXISTS `breadcrumb` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `type` TEXT NOT NULL, `category` TEXT NOT NULL DEFAULT '', `message` TEXT NOT NULL DEFAULT '', `content` TEXT NOT NULL DEFAULT '', `extra` TEXT NOT NULL DEFAULT '', `time` INTEGER NOT NULL)")
            }

        }
    }
}