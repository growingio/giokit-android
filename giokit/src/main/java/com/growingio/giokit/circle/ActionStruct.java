package com.growingio.giokit.circle;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by xyz on 15/12/25.
 */
// FIXME: 16/1/16 ActionStruct和ViewNode的功能略微有点重叠
public class ActionStruct {
    private static final String TAG = "GIO.ActionStruct";
    public LinkedString xpath;
    public long time;
    public int index = -1;
    public String content;
    public String obj;
    public String imgHashcode;

    private volatile int hashCode;

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = 17;
            result = result * 31 + (xpath != null ? xpath.hashCode() : 0);
            result = result * 31 + index;
            result = result * 31 + (content != null ? content.hashCode() : 0);
            result = result * 31 + (obj != null ? obj.hashCode() : 0);
            hashCode = result;
        }

        return result;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "tm: %d, xpath: %s, idx: %d, content: %s", time, xpath, index, content);
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("x", xpath);
            jsonObject.put("tm", System.currentTimeMillis());
            if (index >= 0) {
                jsonObject.put("idx", index);
            }

            if (!TextUtils.isEmpty(obj)) {
                jsonObject.put("obj", obj);
            }

            if (!TextUtils.isEmpty(content)) {
                jsonObject.put("v", content);
            }

            if (!TextUtils.isEmpty(imgHashcode)) {
                jsonObject.put("img", imgHashcode);
            }
        } catch (JSONException e) {
            return null;
        }
        return jsonObject;
    }
}
