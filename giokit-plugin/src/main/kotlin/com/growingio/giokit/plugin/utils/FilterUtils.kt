/*
 *   Copyright (c) 2022 Beijing Yishu Technology Co., Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.growingio.giokit.plugin.utils

import com.growingio.android.plugin.utils.isAndroidGenerated
import com.growingio.giokit.plugin.transform.HookInjectorClass.getIncludePackage


/**
 * <p>
 *
 * @author cpacm 2022/3/30
 */
internal fun shouldClassModified(
    className: String
): Boolean {
    if (isAndroidGenerated(className)) {
        return false
    }

    INCLUDE_PACKAGES.forEach {
        if (className.startsWith(it)) {
            return true
        }
    }

    EXCLUDED_PACKAGES.forEach {
        if (className.startsWith(it)) {
            return false
        }
    }
    return true
}


val INCLUDE_PACKAGES = getIncludePackage()

val EXCLUDED_PACKAGES = hashSetOf(
    "com.growingio.android",
    "com.growingio.giokit",
    //"com.alibaba.mobileim.extra.xblink.webview",
    //"com.alibaba.sdk.android.feedback.xblink",
    //"com.tencent.smtt",
    //"android.taobao.windvane.webview",
    "com.sensorsdata.analytics", //sensorsdata
    "com.blueware.agent.android", //bluware
    "com.oneapm.agent.android", //OneAPM
    "com.networkbench.agent",//tingyun

    // OFFICIAL
    //"androidx",
    //"android.support",
    //"org.jetbrains.kotlin",
    "android.arch",
    "kotlin",
    "android",
    "androidx",
    "com.google",
    "okio",
    "com.github.ybq.android",
    "io.mattcarroll.hover",
    "org.intellij",
    //"com.google.android",

    //THIRD
    "com.bumptech.glide",
    "io.rectivex.rxjava",
    "com.baidu.location",
    "com.qiyukf",
    "com.tencent.smtt",
    "com.umeng.message",
    "com.xiaomi.push",
    "com.huawei.hms",
    "cn.jpush.android",
    "cn.jiguang",
    "com.meizu.cloud.pushsdk",
    "com.vivo.push",
    "com.igexin",
    "com.getui",
    "com.xiaomi.mipush.sdk",
    "com.heytap.msp.push",
    "com.tencent.tinker",
    "com.amap.api",
    "com.google.iot",

    )