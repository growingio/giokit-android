package com.growingio.giokit.launch.db

import androidx.room.*

/**
 * <p>
 *
 * @author cpacm 2022/10/21
 */
@Dao
interface GioKitBreadCrumbDao {

    @Query("SELECT * FROM breadcrumb WHERE id=:id")
    fun getBreadCrumb(id: Long): GioKitBreadCrumb

    @Query("SELECT * FROM breadcrumb WHERE type=:type ORDER BY time DESC LIMIT :start,:limit")
    fun getBreadCrumbList(type: String, start: Int, limit: Int): List<GioKitBreadCrumb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg entities: GioKitBreadCrumb)

    @Query("DELETE FROM breadcrumb WHERE time <=:dayAgo")
    fun outdatedBreadCrumb(dayAgo: Long)

    @Delete
    fun delete(entity: GioKitBreadCrumb)

    @Query("SELECT COUNT(*) FROM breadcrumb WHERE time>=:startTime")
    suspend fun countHttpRequest(startTime: Long): Int

    @Query("DELETE FROM breadcrumb")
    fun clear()
}