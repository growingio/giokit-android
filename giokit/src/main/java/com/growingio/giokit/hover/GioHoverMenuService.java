/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.growingio.giokit.hover;

import android.content.Context;
import android.content.Intent;
import android.view.ContextThemeWrapper;

import androidx.annotation.NonNull;

import com.growingio.giokit.GioKit;
import com.growingio.giokit.R;

import io.mattcarroll.hover.HoverMenu;
import io.mattcarroll.hover.HoverView;
import io.mattcarroll.hover.window.HoverMenuService;

/**
 * GioHoverService {@link HoverMenuService}.
 */
public class GioHoverMenuService extends HoverMenuService {

    private static final String TAG = "GioHoverMenuService";

    public static void showFloatingMenu(Context context) {
        context.startService(new Intent(context, GioHoverMenuService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected Context getContextForHoverMenu() {
        return new ContextThemeWrapper(this, R.style.GioHoverTheme);
    }

    @Override
    protected void onHoverMenuLaunched(@NonNull Intent intent, @NonNull HoverView hoverView) {
        hoverView.setMenu(createHoverMenu());
        hoverView.collapse();

        GioKit.Companion.getGioKitHoverManager().setupHoverView(hoverView);
    }

    private HoverMenu createHoverMenu() {
        return new GioHoverCreateFactory().createGioMenuFromCode(getContextForHoverMenu());
    }
}
