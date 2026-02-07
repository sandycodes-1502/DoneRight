package com.sandycodes.doneright.data.local.database

import androidx.room.TypeConverter
import com.sandycodes.doneright.data.local.Entity.TaskStatus

class TaskStatusConverter {

    @TypeConverter
    fun fromStatus(status: TaskStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(status: String): TaskStatus {
        return TaskStatus.valueOf(status)
    }
}
