package com.sandycodes.doneright.data.local.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tasks")
data class TaskEntity (
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String?,
    val status: TaskStatus = TaskStatus.TODO,
    val updatedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)