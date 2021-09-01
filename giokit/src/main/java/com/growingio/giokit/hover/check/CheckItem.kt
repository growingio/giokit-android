package com.growingio.giokit.hover.check

/**
 * <p>
 *
 * @author cpacm 2021/8/16
 */
data class CheckItem(
    val index: Int,
    val loading: String,
    val title: String,
    val content: String,
    val isError: Boolean = false
){
    var checked: Boolean = false
}
