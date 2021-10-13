package com.growingio.giokit.v3;

import android.widget.Button;

import com.growingio.android.sdk.autotrack.GrowingAutotracker;
import com.growingio.giokit.hook.GioTrackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *
 * @author cpacm 2021/8/23
 */
public class LambdaTest {

    public void test(Button button) {
        button.setOnClickListener(v -> {
            GrowingAutotracker.get().trackCustomEvent("lambda");
        });

        ArrayList<String> list = new ArrayList<>();
        list.add("");

        List arrayList = new ArrayList();
        arrayList.add("cpacm#cpacm");
        GioTrackInfo.inject(arrayList);
    }

    public void doubleText() {
        GrowingAutotracker.get().trackCustomEvent("double1");
        GrowingAutotracker.get().trackCustomEvent("double2");
    }

    public void third() {
        GrowingAutotracker.get().trackCustomEvent("double1");
        GrowingAutotracker.get().trackCustomEvent("double2");
        GrowingAutotracker.get().trackCustomEvent("double3");
    }
}
