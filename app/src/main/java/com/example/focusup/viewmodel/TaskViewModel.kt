package com.example.focusup.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.focusup.data.db.DatabaseProvider
import com.example.focusup.data.model.TaskEntity
import com.example.focusup.data.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository: TaskRepository
    private val _taskList = MutableLiveData<List<TaskEntity>>()
    val taskList: LiveData<List<TaskEntity>> get() = _taskList

    init {
        val db = DatabaseProvider.getDatabase(application)
        taskRepository = TaskRepository(db.taskDao())
    }

    fun loadTasksForUser(userId: Int) {
        viewModelScope.launch {
            val list = taskRepository.getTasksByUser(userId)
            _taskList.postValue(list)
        }
    }

    fun loadTasksByDate(userId: Int, fecha: String) {
        viewModelScope.launch {
            val list = taskRepository.getTasksByDate(userId, fecha)
            _taskList.postValue(list)
        }
    }

    fun addTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.addTask(task)
            loadTasksForUser(task.userId)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
            loadTasksForUser(task.userId)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
            loadTasksForUser(task.userId)
        }
    }
}