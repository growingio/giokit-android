GioKit--GrowingIO SDK 开发辅助工具
======
GioKit 是一个辅助客户快速接入我们SDK的一个功能，它能够让每一个 App 快速检查SDK是否集成成功，而且能够在面板中实时反馈接入的SDK信息，包括接入的SDK信息，App内埋点信息，埋点发送情况和圈选辅助工具等。

> 目前 GioKit 还处于测试阶段，后续还会推出更多的功能。


## 集成GioKit

GioKit 代码已托管在 [Github](https://github.com/growingio/giokit-android) 上，欢迎 star,fork 一波。

> **Gradle插件版本**： 3.2.1及以上  
> **Android系统版本**：Android 5.0及以上
> **仅支持AndroidX**

### 添加依赖
在 project 级别的build.gradle文件中添加giokit-plugin依赖和maven仓库。

```groovy
buildscript {
    repositories {
        // 添加maven仓库
        mavenCentral()
        maven { url "https://s01.oss.sonatype.org/content/repositories/snapshots/" }
        
    }
    dependencies {
        
        //GioKit plugin
        classpath "com.growingio.giokit:giokit-plugin:1.0.0-SNAPSHOT"
    }
}

allprojects {
    repositories {
        // 添加maven仓库
        mavenCentral()
        maven { url "https://s01.oss.sonatype.org/content/repositories/snapshots/" }
    }
}
```

在 app级别的 `build.gradle` 文件中添加 `com.growingio.giokit` 插件、`giokit`依赖。
```groovy
apply plugin: 'com.android.application'

//添加 GioKit 插件，针对SDK 3.0
apply plugin: 'com.growingio.giokit'
//或者如果是SDK2.0
apply plugin: 'com.growingio.giokit.saas'
//以上二者根据相应的sdk选择对应的插件，请不要一起使用！！
...

dependencies {
    ...
    //GioKit
    debugImplementation "com.growingio.giokit:giokit:1.0.0-SNAPSHOT"
    releaseImplementation "com.growingio.giokit:giokit-no-op:1.0.0-SNAPSHOT"
}

```
> 为了避免在正式环境下出现不必要的错误，请务必只在Debug环境下使用 GioKit 工具。

### 初始化
请将 GioKit 的初始化代码放入 `Application` 的 `onCreate` 中。

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GioKit.with(this).build();
    }
}
```

### 插件配置
为了满足用户的需求，我们在 Giokit 插件中添加了多项配置。
```groovy
giokitExt {
    debugMode false
    enableIncremental false
    enableRelease false
    // 统计该域值下所有埋点信息，如 com.growingio 表示统计 com.growingio 包名下的埋点代码
    trackFinder {
        domain = ["com.growingio.giokit.demo"]
        // 若用户自己封装了SDK customEvent 方法，可通过在此设置来查找封装类调用的代码
        // className "com.growingio.giokit.demo.AutotrackerUtils"
        // methodName "trackCustomEvent"
    }
}
```
1. debugMode 为true时，项目编译的时候会输出相应的 Debug 信息；
2. enableIncremental 是否打开增量编译，默认为true。由于 Giokit 会查找代码中的所有埋点信息，所以在增量编译时有时候会出现无法找到埋点代码的情况，这时候可以将其设置false，来保证每次查找的准确性；
3. enableRelease 是否支持release打包。giokit 是只推荐在 debug 环境下使用，若一定要在release环境下使用，则需要打开此开关来使插件生效；
4. 为了方便统一查看用户的手动埋点信息，我们通过 trackFinder 配置来查找在应用中手动埋点调用的位置。默认查找域名为项目的`ApplicationId`

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
使用该功能可以显示界面中控件的path路径，目前只兼容WebView。

![sdk path](https://github.com/growingio/giokit-android/blob/master/ScreenShot/sdktrack.gif?raw=true)

### 网络请求
该界面会显示应用运行期间产生的所有网络请求，包括请求数量，请求大小和请求错误的个数。同时在详情页内可以查看使用了加密库之后的请求数据。

![sdk path](https://github.com/growingio/giokit-android/blob/master/ScreenShot/sdkhttp.gif?raw=true)
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
