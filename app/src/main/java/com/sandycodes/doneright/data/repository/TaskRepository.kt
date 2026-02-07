package com.sandycodes.doneright.data.repository

import android.util.Log
import com.sandycodes.doneright.data.local.Dao.TaskDao
import com.sandycodes.doneright.data.local.Entity.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao
) {

    fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDao.getAllTasks()
    }

    suspend fun insertTask(task: TaskEntity) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        Log.d("TASK_REPO", "DB update: ${task.title} → ${task.status}")
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    fun getActiveTasks(): Flow<List<TaskEntity>> {
        return taskDao.getActiveTasks()
    }

    fun getCompletedTasks(): Flow<List<TaskEntity>> {
        return taskDao.getCompletedTasks()
    }
}
