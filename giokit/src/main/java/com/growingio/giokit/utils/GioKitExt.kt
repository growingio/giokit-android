package com.growingio.giokit.utils

/**
 * <p>
 *
 * @author cpacm 2021/10/13
 */
fun <E> ArrayList<E>.tryAdd(action: () -> E): Boolean {
    return try {
        this.add(action())
    } catch (e: Exception) {
        false
    }
}