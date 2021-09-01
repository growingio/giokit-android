package com.growingio.giokit.launch.sdkcode

/**
 * <p>
 *
 * @author cpacm 2021/8/30
 */
data class SdkCode(val className: String) {
    val methodList = hashSetOf<String>()

    fun addMethod(method: String) {
        methodList.add(method)
    }
}


fun dealWithTrackCode(list: HashSet<String>): List<SdkCode> {
    val sdkCodeMap = hashMapOf<String, SdkCode>()
    list.forEach {
        val (className, methodName) = it.split("::")
        if (sdkCodeMap.get(className) == null) {
            sdkCodeMap.put(className, SdkCode(className))
        }
        sdkCodeMap.get(className)?.addMethod(methodName)
    }
    val sdkCodeList = arrayListOf<SdkCode>()
    sdkCodeList.addAll(sdkCodeMap.values)
    return sdkCodeList

}