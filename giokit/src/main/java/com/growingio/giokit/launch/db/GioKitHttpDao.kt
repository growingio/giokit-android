package com.growingio.giokit.launch.db

import androidx.room.*

/**
 * <p>
 *
 * @author cpacm 2021/10/28
 */
@Dao
interface GioKitHttpDao {

    @Query("SELECT * FROM https WHERE id=:id")
    fun getHttp(id: Int): GioKitHttpBean

    @Query("SELECT * FROM https ORDER BY httpTime DESC LIMIT :start,:limit")
    fun getHttpList(start: Int, limit: Int): List<GioKitHttpBean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg entities: GioKitHttpBean)

    @Query("DELETE FROM https WHERE httpTime <=:dayAgo")
    fun outdatedHttp(dayAgo: Long)

    @Delete
    fun delete(entity: GioKitHttpBean)

    @Query("SELECT COUNT(*) FROM https WHERE httpTime>=:startTime")
    suspend fun countHttpRequest(startTime: Long): Int

    @Query("SELECT SUM(requestSize) FROM https WHERE httpTime>=:startTime")
    suspend fun sumHttpUploadData(startTime: Long): Long?

    @Query("SELECT COUNT(*) FROM https WHERE httpTime>=:startTime AND (responseCode<200 OR responseCode>=300)")
    suspend fun countHttpErrorRequest(startTime: Long): Int

    @Query("DELETE FROM https")
    fun clear()
}