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
package com.growingio.giokit.circle;

import android.content.Context;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.growingio.giokit.R;

class CircleExitView extends FloatViewContainer {

    private static final String TAG = "CircleExitView";

    private TextView mNodeTv;
    public CircleExitView(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.giokit_view_circle_exit, this, true);
        mNodeTv = findViewById(R.id.view_circle_node);
    }

    public void setNodeInfo(String nodeInfo){
        mNodeTv.setText(nodeInfo);
    }

    public void setNodeInfo(Spannable sp){
        mNodeTv.setText(sp);
    }
}
