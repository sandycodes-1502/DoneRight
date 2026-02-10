package com.sandycodes.doneright.data.repository

import android.util.Log
import com.sandycodes.doneright.data.local.Dao.TaskDao
import com.sandycodes.doneright.data.local.Entity.TaskEntity
import com.sandycodes.doneright.data.local.Entity.TaskStatus
import com.sandycodes.doneright.data.remote.FirestoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TaskRepository(
    private val taskDao: TaskDao
) {

    fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDao.getAllTasks()
    }

    fun syncFromFirestore() {
        FirestoreService.listenTasks { remoteTasks ->
            CoroutineScope(Dispatchers.IO).launch {
                remoteTasks.forEach { remote ->
                    val local = taskDao.getTaskByRemoteId(remote.id)

                    val status = runCatching {
                        TaskStatus.valueOf(remote.status)
                    }.getOrDefault(TaskStatus.TODO)

                    if (local == null) {
                        taskDao.insertTask(
                            TaskEntity(
                                id = remote.id,
                                title = remote.title,
                                description = remote.description,
                                status = status,
                                createdAt = remote.createdAt,
                                updatedAt = remote.updatedAt,
                            )
                        )
                    } else if (remote.updatedAt > local.updatedAt) {
                        taskDao.updateTask(
                            local.copy(
                                title = remote.title,
                                description = remote.description,
                                status = TaskStatus.valueOf(remote.status),
                                updatedAt = remote.updatedAt
                            )
                        )
                    }
                }
            }
        }
    }


    suspend fun insertTask(task: TaskEntity) {
        taskDao.insertTask(task)
        FirestoreService.upsertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        Log.d("TASK_REPO", "DB update: ${task.title} → ${task.status}")
        taskDao.updateTask(task)
        FirestoreService.upsertTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
        FirestoreService.deleteTask(task)
    }
    suspend fun clearLocalTasks() {
        taskDao.clearAllTasks()
    }
}
