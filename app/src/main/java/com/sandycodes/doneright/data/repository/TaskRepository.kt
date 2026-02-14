package com.sandycodes.doneright.data.repository

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.sandycodes.doneright.data.local.Dao.TaskDao
import com.sandycodes.doneright.data.local.Entity.TaskEntity
import com.sandycodes.doneright.data.local.Entity.TaskStatus
import com.sandycodes.doneright.data.local.notificationReminder.TaskReminderWorker
import com.sandycodes.doneright.data.remote.firebase.FirestoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TaskRepository(
    private val context: Context,
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
        scheduleTaskReminder(context, task.id)
    }

    suspend fun updateTask(task: TaskEntity) {
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

    fun scheduleTaskReminder(context: Context, taskId: String) {

        val data = workDataOf("TASK_ID" to taskId)

        val request = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInputData(data)
            .setInitialDelay(9, TimeUnit.HOURS)
            .addTag(taskId)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

}
