package com.sandycodes.doneright.data.local.notificationReminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sandycodes.doneright.data.remote.quotes.QuoteRepository

class QuoteWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        val repository = QuoteRepository()
        val quote = repository.fetchQuote()

        NotificationHelper.showNotification(
            applicationContext,
            "${quote.author} said:",
            quote.quote
        )

        return Result.success()
    }
}
