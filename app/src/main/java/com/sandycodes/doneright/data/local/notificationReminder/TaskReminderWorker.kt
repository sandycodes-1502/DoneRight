package com.sandycodes.doneright.data.local.notificationReminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sandycodes.doneright.data.local.Entity.TaskStatus
import com.sandycodes.doneright.data.local.database.DoneRightDatabase

class TaskReminderWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val taskId = inputData.getString("TASK_ID") ?: return Result.failure()

        val database = DoneRightDatabase.getInstance(applicationContext)
        val taskDao = database.taskDao()

        val task = taskDao.getTaskByRemoteId(taskId) ?: return Result.success()

        val now = System.currentTimeMillis()
        val diff = now - task.createdAt
        val hours12 = 12 * 60 * 60 * 1000L

        if (diff >= hours12) {
            if (task.status == TaskStatus.TODO || task.status == TaskStatus.IN_PROGRESS) {
                NotificationHelper.showNotification(
                    applicationContext,
                    "You still haven't updated ${task.title}",
                    getRandomReminderLine()
                )
            }
        }
        return Result.success()
    }

    private fun getRandomReminderLine(): String {
        val reminders = listOf(
            "Your task has been chilling for 24 hours. You good? 👀",
            "Still pending. Still waiting. Just like your future.",
            "One small update. That’s all it takes.",
            "Momentum > Motivation. Just start.",
            "Done is better than perfect.",
            "Future you is judging right now.",
            "This task has been chilling for 24 hours. You good? 👀",
            "Still pending. Still waiting. Just like your future.",
            "One small update. That’s all it takes.",
            "Your task hasn’t moved. Neither has your progress.",
            "24 hours passed. Action didn’t.",
            "You added it for a reason. Remember?",
            "Momentum > Motivation. Just start.",
            "Done is better than perfect.",
            "Future you is judging right now.",
            "DoneRight doesn’t mean done later."
        )
        return reminders.random()
    }
}
