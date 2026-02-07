package com.sandycodes.doneright.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sandycodes.doneright.data.local.Entity.TaskEntity
import com.sandycodes.doneright.data.local.Entity.TaskStatus
import com.sandycodes.doneright.data.repository.TaskRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    val tasks: LiveData<List<TaskEntity>> = repository.getAllTasks().asLiveData()

    fun updateTaskStatus(task: TaskEntity) {
        val newStatus = when (task.status) {
            TaskStatus.TODO -> TaskStatus.TODO
            TaskStatus.IN_PROGRESS -> TaskStatus.IN_PROGRESS
            TaskStatus.DONE -> TaskStatus.DONE
        }

        viewModelScope.launch {
            repository.updateTask(
                task.copy(
                    status = newStatus,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

}