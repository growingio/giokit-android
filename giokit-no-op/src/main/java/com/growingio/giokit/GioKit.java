package com.growingio.giokit;

import android.app.Application;

/**
 * <p>
 * 对外接口
 *
 * @author cpacm 2022/3/8
 */
public class GioKit {

    private GioKit() {
    }

    public static Builder with(Application application) {
        return new Builder(application);
    }

    public static class Builder {
        Builder(Application app) {
        }

        public void build() {
        }
    }
}
