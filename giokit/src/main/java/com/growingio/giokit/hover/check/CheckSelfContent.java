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
package com.growingio.giokit.hover.check;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growingio.giokit.R;
import com.growingio.giokit.utils.CheckSdkStatusManager;

import io.mattcarroll.hover.Content;

/**
 * {@link Content} that displays an checkview.
 */
public class CheckSelfContent extends FrameLayout implements Content {

    private static final float POST_TIME_DELAY = 800L;

    private View checkButton;
    private View descLayout;
    private HoverMotion mHoverMotion;
    private RecyclerView checkList;
    private CheckAdapter checkAdapter;

    public CheckSelfContent(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.giokit_view_content_checkself, this, true);

        checkButton = findViewById(R.id.checkButton);
        descLayout = findViewById(R.id.descLayout);
        checkButton.setOnClickListener(v -> {
            startCheckSelf();
        });

        checkAdapter = new CheckAdapter(getContext());
        checkList = findViewById(R.id.checkList);
        checkList.setLayoutManager(new LinearLayoutManager(getContext()));
        checkList.setAdapter(checkAdapter);

        mHoverMotion = new HoverMotion();
    }


    private void startCheckSelf() {
        checkAdapter.clearData();
        getHandler().removeCallbacksAndMessages("giosdk");
        descLayout.setVisibility(View.GONE);
        checkList.setVisibility(View.VISIBLE);
        long postTime = SystemClock.uptimeMillis();

        post(CheckSdkStatusManager.getInstance().getSdkDepend(0), postTime += 100L);
        post(CheckSdkStatusManager.getInstance().hasSdkPlugin(1), postTime += POST_TIME_DELAY);
        post(CheckSdkStatusManager.getInstance().getProjectStatus(2), postTime += POST_TIME_DELAY);
        post(CheckSdkStatusManager.getInstance().getProjectID(3), postTime += POST_TIME_DELAY);
        post(CheckSdkStatusManager.getInstance().getURLScheme(4), postTime += POST_TIME_DELAY);
        post(CheckSdkStatusManager.getInstance().getDataSourceID(5), postTime += POST_TIME_DELAY);
        post(CheckSdkStatusManager.getInstance().getDataServerHost(6), postTime += POST_TIME_DELAY);
        post(CheckSdkStatusManager.getInstance().getDataCollectionEnable(7), postTime += POST_TIME_DELAY);
        post(CheckSdkStatusManager.getInstance().getSdkDebug(8), postTime += POST_TIME_DELAY);
        post(CheckSdkStatusManager.getInstance().getOaidEnabled(9), postTime += POST_TIME_DELAY);
        //最后是手动埋点个数
        CheckItem trackItem = CheckSdkStatusManager.getInstance().getTrackCount(10);
        if (!trackItem.isError()) {
            post(trackItem, postTime += 1000L);
        }

        getHandler().postAtTime(() -> {
            mHoverMotion.stop();
        }, "giosdk", postTime + 1000L);
    }

    private void post(CheckItem checkItem, long time) {
        getHandler().postAtTime(() -> {
            checkAdapter.addData(checkItem);
        }, "giosdk", time);
        getHandler().postAtTime(() -> {
            checkItem.setChecked(true);
            checkAdapter.updateData(checkItem);
        }, "giosdk", time + 500);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public boolean isFullscreen() {
        return true;
    }

    @Override
    public void onShown() {
        if (descLayout.getVisibility() == VISIBLE) mHoverMotion.start(checkButton);
    }

    @Override
    public void onHidden() {
        mHoverMotion.stop();
    }



}
