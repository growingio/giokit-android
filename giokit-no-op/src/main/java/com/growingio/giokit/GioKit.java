package com.growingio.giokit;

import android.app.Activity;
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

        public Builder attach(boolean attach) {
            return this;
        }

        public Builder setGMonitorOption(Object option) {
            return this;
        }

        public void build() {
        }
    }

    public static void attach(Activity activity) {
    }

    public static void detach(Activity activity) {
    }
}
