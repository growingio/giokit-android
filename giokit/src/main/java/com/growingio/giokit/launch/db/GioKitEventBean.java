package com.growingio.giokit.launch.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * <p>
 * giokit 保存 events 表
 *
 * @author cpacm 2021/8/30
 */
@Entity(tableName = "events", indices = {@Index(value = {"gsid"}, unique = true)})
public class GioKitEventBean {

    @Ignore
    public static final int STATUS_READY = 0;
    @Ignore
    public static final int STATUS_SENDED = 1;
    @Ignore
    public static final int STATUS_OUTDATE = -1;

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "gsid")
    private long gsid;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "status")
    private int status;//0，1，-1=>未发送，已发送，过期

    @ColumnInfo(name = "data")
    private String data;//数据内容

    @ColumnInfo(name = "path")
    private String path;//路径，可为空

    @ColumnInfo(name = "time")
    private long time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getGsid() {
        return gsid;
    }

    public void setGsid(long gsid) {
        this.gsid = gsid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
