package com.example.guillaumebranche.todo.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guillaumebranche.todo.network.TasksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskListViewModel : ViewModel() {
    private val tasksRepository = TasksRepository()
    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    val taskList: StateFlow<List<Task>> = _taskList

    fun loadTasks() {
        viewModelScope.launch {
            _taskList.value = tasksRepository.loadTasks() ?: emptyList()
        }
    }
    fun deleteTask(task: Task) {
        val oldTask = _taskList.value.firstOrNull { it.id == task.id }
        if (oldTask != null) {
            viewModelScope.launch {
                tasksRepository.deleteTask(oldTask)
            }
         }
    }
    fun addTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.createTask(task)
        }

        _taskList.value = _taskList.value + task
    }

    fun editTask(task: Task) {
        val oldTask = _taskList.value.firstOrNull { it.id == task.id }
        if (oldTask != null) {
            viewModelScope.launch {
                tasksRepository.updateTask(task)
            }
            _taskList.value = _taskList.value - oldTask + task
        }

    }
}
