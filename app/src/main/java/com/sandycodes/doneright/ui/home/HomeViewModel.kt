package com.sandycodes.doneright.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.sandycodes.doneright.data.local.Entity.TaskEntity
import com.sandycodes.doneright.data.repository.TaskRepository

class HomeViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    val tasks: LiveData<List<TaskEntity>> = repository.getAllTasks().asLiveData()

}