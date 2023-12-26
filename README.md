GioKit--GrowingIO SDK 开发辅助工具
======
GioKit 是一个辅助客户快速接入我们SDK的一个功能，它能够让每一个 App 快速检查SDK是否集成成功，而且能够在面板中实时反馈接入的SDK信息，包括接入的SDK信息，App内埋点信息，埋点发送情况和圈选辅助工具等。

> 目前 GioKit 还处于测试阶段，后续还会推出更多的功能。


## 集成GioKit

GioKit 代码已托管在 [Github](https://github.com/growingio/giokit-android) 上，欢迎 star,fork。

> **Gradle插件版本**： 4.0.1及以上  
> **Android系统版本**：Android 5.0及以上
> **仅支持AndroidX**


为了方便能够快速配置Giokit,现在可以直接在 [GrowingIO SDK的plugin](https://github.com/growingio/growingio-sdk-android-plugin)中直接通过配置集成。

| Extension                    | 参数类型         | 是否必填 | 默认值 | 说明 |  版本 |
| :-------------------------   | :------         | :----:  |:------  |:------| --------------------------   |
| logEnabled                 | _Boolean_       | 否      | `false`  | 编译时是否输出log日志          |  |
| skipDependencyCheck       | _Boolean_       | 否      | `false`  | 编译时检测当前project是否配置SDK依赖（模块中依赖时配置为true）          |  |
| includePackages            | _Array<String\>_ | 否      | `null`   | 需要额外包含编译的包名          |  |
| excludePackages            | _Array<String\>_ | 否      | `null`   | 需要跳过编译的包名             |  |
| giokit                     | _GiokitExtension_ | 否    | `null`   | 可以用来配置是否引入 Giokit | | 

配置代码示例
```groony
plugins {
    ···
    // 使用 GrowingIO 无埋点 SDK 插件
    id 'com.growingio.android.autotracker'
}

growingAutotracker {
    logEnabled false
    includePackages "com.growingio.xxx1","com.growingio.xxx2"
    excludePackages "com.cpacm.xxx1"
    giokit {
        //...
    }
}


dependencies {
  ···
}
```

### Giokit 配置

| Extension                    | 参数类型         | 是否必填 | 默认值 | 说明 |
| :-------------------------   | :------         | :----:  |:------  |:------|
| enabled                   | _Boolean_       | 否      | `false`  |  是否添加 Giokit        |
| trackerFinderEnabled      | _Boolean_       | 否      | `true`  | 查找App下调用App埋点接口的信息      |
| trackerFinderDomain        | _Array<String\>_ | 否      | 默认为应用 ApplicationId   | 查找的范围  |
| trackerCalledMethod        | _Array<String\>_ | 否      | 默认为SDK相应接口   | 要查找的类和方法,类名与方法名使用#连接  |
| autoAttachEnabled          | _Boolean_       | 否      | `true`  |  GioKit 是否自动依附在Activity上，若设为false，需要自行调用api打开GioKit  |
| releaseEnabled             | _Boolean_       | 否      | `false`   |  **请不要打开**，否则会在 Release 打包中包含 GioKit 代码    |
| autoInstallVersion         | _String_        | 否      | `2.0.0`   |  自动依赖的GioKit版本号             |

现在SDK不用再额外引入 Giokit，只需要在插件中开启即可。示例如下：

```groovy
growingAutotracker {
    logEnabled true
    giokit {
        enabled false  //开启则可引入 GioKit
        trackerFinderEnabled true
        trackerFinderDomain "com.xxxx.yourapplication"
        trackerCalledMethod "com.growingio.android.tracker#trackCumtomEvent"
        autoAttachEnabled true
        releaseEnabled false
        autoInstallVersion "2.0.0"
    }
}
```

## 功能

### 自检页
自检页可以帮助你获取最基本的SDK集成信息，如下图所示：

![checkself](https://github.com/growingio/giokit-android/blob/master/ScreenShot/checkself.gif?raw=true)

> 若标注红字则说明该项设置可能在正式环境下会有隐患。

### SDK信息
该页收集了应用的所有基本信息，你可以在这里查看应用的详细信息。

![sdk info](https://github.com/growingio/giokit-android/blob/master/ScreenShot/sdkinfo.gif?raw=true)

### 代码埋点
这项功能会帮助你查找代码中所有的手动埋点位置，以 “类+方法” 的列表形式展现

![sdk track](https://github.com/growingio/giokit-android/blob/master/ScreenShot/sdkcode.jpg?raw=true)

### 埋点数据
该界面会按照时间来展示埋点数据以及发送状况。
同时你可以通过输入事件类型来进行筛选。
![sdk data](https://github.com/growingio/giokit-android/blob/master/ScreenShot/sdkdata.gif?raw=true)

### 埋点追踪
使用该功能可以显示界面中控件的path路径。

![sdk path](https://github.com/growingio/giokit-android/blob/master/ScreenShot/sdktrack.gif?raw=true)

> 请注意，如果是需要计算webview上的xpath,则 `webview` 必须设置一个 `WebChromeClient` 以提供 Giokit 的js注入点。如下所示
```java
  webView.setWebChromeClient(new WebChromeClient() {});
```

### 网络请求
该界面会显示应用运行期间产生的所有网络请求，包括请求数量，请求大小和请求错误的个数。同时在详情页内可以查看使用了加密库之后的请求数据。

![sdk path](https://github.com/growingio/giokit-android/blob/master/ScreenShot/sdkhttp.gif?raw=true)

### 错误报告
该界面会捕获应用运行期间发生的Java错误，包括ANR错误。在详情页可以查看错误的堆栈信息。

### 启动耗时
启动耗时界面会统计应用每个界面打开的耗时时间，包括app冷热启动时间，Activity启动时间，Fragment启动时间。

## License
```
Copyright (C) 2020 Beijing Yishu Technology Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
