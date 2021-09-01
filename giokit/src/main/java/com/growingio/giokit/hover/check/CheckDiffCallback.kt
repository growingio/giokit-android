package com.growingio.giokit.hover.check

import androidx.recyclerview.widget.DiffUtil

/**
 * <p>
 *
 * @author cpacm 2021/8/16
 */
class CheckDiffCallback(val oldData: List<CheckItem>, val newData: List<CheckItem>) :
    DiffUtil.Callback() {


    override fun getOldListSize(): Int {
        return oldData.size
    }

    override fun getNewListSize(): Int {
        return newData.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData.get(oldItemPosition).index == newData.get(newItemPosition).index
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData.get(oldItemPosition) == newData.get(newItemPosition)
    }
}