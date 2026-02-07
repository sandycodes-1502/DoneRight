package com.sandycodes.doneright.ui.model

import com.sandycodes.doneright.data.local.Entity.TaskEntity

sealed class HomeItem {
    data class Header(val title: String) : HomeItem()
    data class TaskItem(val task: TaskEntity) : HomeItem()
}