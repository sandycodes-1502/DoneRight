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
import com.sandycodes.doneright.data.remote.FirebaseGoogleAuthManager
import com.sandycodes.doneright.data.repository.TaskRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    val tasks: LiveData<List<TaskEntity>> =
        repository.getAllTasks()
            .map { list ->
                list.sortedWith (
                    compareBy<TaskEntity> { priority(it.status) }
                    .thenByDescending { it.updatedAt }
                )
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

    fun onGoogleSignInResult(result: FirebaseGoogleAuthManager.AuthResult) {
        viewModelScope.launch {
            when (result) {
                FirebaseGoogleAuthManager.AuthResult.LinkedAnonymous -> {
                    repository.syncFromFirestore()
                }

                FirebaseGoogleAuthManager.AuthResult.SignedInExisting -> {
                    repository.clearLocalTasks()
                    repository.syncFromFirestore()
                }
            }
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


    private fun priority(status: TaskStatus): Int {
        return when (status) {
            TaskStatus.TODO -> 0
            TaskStatus.IN_PROGRESS -> 1
            TaskStatus.DONE -> 2
        }
    }


}