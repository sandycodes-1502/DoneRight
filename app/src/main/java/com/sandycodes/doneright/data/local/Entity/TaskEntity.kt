package com.sandycodes.doneright.data.local.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String?,
    val status: TaskStatus = TaskStatus.TODO,
    val updatedAt: Long = System.currentTimeMillis()
)