package com.sandycodes.doneright.ui.home

import androidx.recyclerview.widget.DiffUtil
import com.sandycodes.doneright.data.local.Entity.TaskEntity

class TaskDiffCallback(
    private val oldList: List<TaskEntity>,
    private val newList: List<TaskEntity>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id ==
                newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] ==
                newList[newItemPosition]
    }
}
