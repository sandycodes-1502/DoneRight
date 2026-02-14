package com.sandycodes.doneright.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sandycodes.doneright.data.local.Entity.TaskEntity
import com.sandycodes.doneright.data.local.Entity.TaskStatus
import com.sandycodes.doneright.data.remote.quotes.Quote
import com.sandycodes.doneright.data.remote.quotes.QuoteRepository
import com.sandycodes.doneright.data.repository.TaskRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: TaskRepository,
    private val quoteRepository: QuoteRepository
) : ViewModel() {

    val tasks: LiveData<List<TaskEntity>> =
        repository.getAllTasks()
            .map { list ->
                list.sortedWith (
                    compareBy<TaskEntity> { priority(it.status) }
                    .thenByDescending { it.updatedAt }
                )
            }.asLiveData()

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    init {
        _loading.value = true

        viewModelScope.launch {
            repository.getAllTasks()
            _loading.postValue(false)
        }
    }

    fun updateTaskStatus(task: TaskEntity) {
        val newStatus = when (task.status) {
            TaskStatus.TODO -> TaskStatus.IN_PROGRESS
            TaskStatus.IN_PROGRESS -> TaskStatus.DONE
            TaskStatus.DONE -> TaskStatus.TODO
        }

        Log.d("TASK_STATUS", "Updating '${task.title}' from ${task.status} → $newStatus")

        viewModelScope.launch {
            repository.updateTask(
                task.copy(
                    status = newStatus,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun insertTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

     fun priority(status: TaskStatus): Int {
         return when (status) {
             TaskStatus.TODO -> 0
             TaskStatus.IN_PROGRESS -> 1
             TaskStatus.DONE -> 2
         }
     }

    private val _quote = MutableLiveData<Quote>()
    val quote: LiveData<Quote> = _quote

    fun loadQuote() {
        viewModelScope.launch {
            val result = quoteRepository.fetchQuote()
            _quote.postValue(result)
        }
    }

}