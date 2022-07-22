package com.growingio.giokit.launch.db

import androidx.room.*

/**
 * <p>
 *
 * @author cpacm 2021/8/30
 */
@Dao
interface GioKitEventDao {

    @Query("SELECT * FROM events WHERE id=:id")
    fun getEvent(id: Int): GioKitEventBean

    @Query("SELECT * FROM events WHERE status=:status ORDER BY time DESC LIMIT :start,:limit")
    fun getEventListWithStatus(status: Int, start: Int, limit: Int): List<GioKitEventBean>

    @Query("SELECT * FROM events ORDER BY time DESC,gsid DESC LIMIT :start,:limit")
    fun getEventList(start: Int, limit: Int): List<GioKitEventBean>

    @Query("SELECT * FROM events WHERE type=:event ORDER BY time DESC,gsid DESC LIMIT :start,:limit")
    fun getEventListByType(event: String, start: Int, limit: Int): List<GioKitEventBean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg entities: GioKitEventBean)

    @Query("UPDATE events SET status=1 WHERE gsid=:gsid")
    fun updateFromGsid(gsid: Long)

    @Query("UPDATE events SET status=-1 WHERE time <=:sevenDayAgo AND status=0")
    fun outdatedEvent(sevenDayAgo: Long)

    @Query("UPDATE events SET status=1 WHERE status=0 AND gsid<=:gsid AND type=:type")
    fun updateLastGsid(gsid: Long, type: String)

    @Query("UPDATE events SET status=1 WHERE extra=:extra")
    fun updateLastExtra(extra: String)

    @Delete
    fun delete(entity: GioKitEventBean)

    @Query("DELETE FROM events")
    fun clear()

}