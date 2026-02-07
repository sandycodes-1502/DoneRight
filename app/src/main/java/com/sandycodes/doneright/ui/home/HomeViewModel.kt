package com.sandycodes.doneright.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.sandycodes.doneright.ui.model.HomeItem
import com.sandycodes.doneright.data.local.Entity.TaskEntity
import com.sandycodes.doneright.data.local.Entity.TaskStatus
import com.sandycodes.doneright.data.repository.TaskRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: TaskRepository
) : ViewModel() {

//    val tasks: LiveData<List<TaskEntity>> = repository.getAllTasks().asLiveData()
//
//    val activeTasks: LiveData<List<TaskEntity>> = repository.getActiveTasks().asLiveData()
//    val completedTasks: LiveData<List<TaskEntity>> = repository.getCompletedTasks().asLiveData()


    val homeItems: LiveData<List<HomeItem>> =
        combine(
            repository.getActiveTasks(),
            repository.getCompletedTasks()
        ) { active, completed ->

            val items = mutableListOf<HomeItem>()

            if (active.isNotEmpty()) {
                items.add(HomeItem.Header("Active Tasks"))
                active.forEach { items.add(HomeItem.TaskItem(it)) }
            }

            if (completed.isNotEmpty()) {
                items.add(HomeItem.Header("Done Tasks"))
                completed.forEach { items.add(HomeItem.TaskItem(it)) }
            }

            items
        }.asLiveData()

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

}