package com.growingio.giokit.launch.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * <p>
 * giokit http 请求缓存表
 *
 * @author cpacm 2021/10/28
 */
@Entity(tableName = "https")
public class GioKitHttpBean {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String requestUrl;
    private String requestMethod;
    private String requestHeader;
    private String requestBody;
    private long requestSize;

    private String responseUrl;
    private int responseCode;
    private String responseHeader;
    private String responseBody;
    private String responseMessage;
    private long responseSize;

    private long httpCost;
    private long httpTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public long getRequestSize() {
        return requestSize;
    }

    public void setRequestSize(long requestSize) {
        this.requestSize = requestSize;
    }

    public String getResponseUrl() {
        return responseUrl;
    }

    public void setResponseUrl(String responseUrl) {
        this.responseUrl = responseUrl;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(String responseHeader) {
        this.responseHeader = responseHeader;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public long getResponseSize() {
        return responseSize;
    }

    public void setResponseSize(long responseSize) {
        this.responseSize = responseSize;
    }

    public long getHttpTime() {
        return httpTime;
    }

    public void setHttpTime(long httpTime) {
        this.httpTime = httpTime;
    }

    public long getHttpCost() {
        return httpCost;
    }

    public void setHttpCost(long httpCost) {
        this.httpCost = httpCost;
    }
}
