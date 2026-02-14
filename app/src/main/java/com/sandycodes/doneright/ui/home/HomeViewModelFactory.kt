package com.sandycodes.doneright.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sandycodes.doneright.data.remote.quotes.QuoteRepository
import com.sandycodes.doneright.data.repository.TaskRepository

class HomeViewModelFactory(
    private val taskRepository: TaskRepository,
    private val quoteRepository: QuoteRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(taskRepository, quoteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
