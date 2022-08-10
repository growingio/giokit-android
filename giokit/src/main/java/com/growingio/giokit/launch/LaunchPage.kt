package com.growingio.giokit.launch

import com.growingio.giokit.launch.sdkcode.SdkCodeFragment
import com.growingio.giokit.launch.sdkdata.SdkDataFragment
import com.growingio.giokit.launch.sdkhttp.SdkHttpFragment
import com.growingio.giokit.launch.sdkinfo.SdkInfoFragment
import com.growingio.giokit.setting.SdkCommonSettingFragment

/**
 * <p>
 *
 * @author cpacm 2021/8/26
 */
object LaunchPage {
    const val LAUNCH_FRAGMENT_INDEX = "launch_page"
    const val LAUNCH_FRAGMENT_TITLE = "launch_title_res"
    const val SDKINFO_PAGE = 1
    const val SDKCODE_PAGE = 2
    const val SDKDATA_PAGE = 3
    const val SDKHTTP_PAGE = 4

    const val SDK_CRASH_PAGE = 10

    const val SDK_COMMONSETTING_PAGE = 20


    fun getPageClass(index: Int) = when (index) {
        SDKINFO_PAGE -> SdkInfoFragment::class.java
        SDKCODE_PAGE -> SdkCodeFragment::class.java
        SDKDATA_PAGE -> SdkDataFragment::class.java
        SDKHTTP_PAGE -> SdkHttpFragment::class.java
        else -> null
    }

    fun getSettingPageClass(index: Int) = when(index){
        SDK_COMMONSETTING_PAGE -> SdkCommonSettingFragment::class.java
        else -> null
    }

}