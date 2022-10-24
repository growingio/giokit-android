package com.growingio.giokit.launch.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * <p>
 *     性能存储
 * @author cpacm 2022/10/19
 */
@Entity(tableName = "breadcrumb")
data class GioKitBreadCrumb(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long = 0L,
    @ColumnInfo(name = "type") var type: String,//类型,目前包括错误日志,加载时长两种
    @ColumnInfo(name = "category", defaultValue = "") var category: String,//分类
    @ColumnInfo(name = "message", defaultValue = "") var message: String, //信息
    @ColumnInfo(name = "content", defaultValue = "") var content: String,
    @ColumnInfo(name = "extra", defaultValue = "") var extra: String,
    @ColumnInfo(name = "time") var time: Long = 0L,
) {
    constructor() : this(0L, "breadcrumb", "", "", "", "", 0L)
}

/**
 * 错误日志包括 java crash,anr,http error三种
 * ‖=======================================‖
 * ‖         NULLPOINTEREXCEPTION          ‖
 * ‖ CRASH   at MainActivity(15:20)  20:18 ‖
 * ‖=======================================‖
 */
const val BREADCRUMB_TYPE_ERROR = "error"

/**
 * 加载时长目前包括 Activity 和 Fragment 两种
 * ‖==========================================‖
 * ‖ MainActivity                             ‖
 * ‖ => Main2Activity              cast 100ms ‖
 * ‖==========================================‖
 */
const val BREADCRUMB_TYPE_PERFORMANCE = "performance"
