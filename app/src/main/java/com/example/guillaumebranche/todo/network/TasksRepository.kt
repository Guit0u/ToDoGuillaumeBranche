package com.example.guillaumebranche.todo.network

import com.example.guillaumebranche.todo.tasklist.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TasksRepository {
    private val tasksWebService = Api.tasksWebService

    // Ces deux variables encapsulent la même donnée:
    // [_taskList] est modifiable mais privée donc inaccessible à l'extérieur de cette classe
    private val _taskList = MutableStateFlow<List<Task>>(value = emptyList())
    // [taskList] est publique mais non-modifiable:
    // On pourra seulement l'observer (s'y abonner) depuis d'autres classes
    val taskList: StateFlow<List<Task>> = _taskList.asStateFlow()

    suspend fun loadTasks(): List<Task>? {
        val response = tasksWebService.getTasks()
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun updateTask(task: Task) {
        val tasksResponse = tasksWebService.update(task)
        if (tasksResponse.isSuccessful) {
            val updatedTask = tasksResponse.body()!!
            val oldTask = taskList.value.firstOrNull { it.id == updatedTask.id }
            if (oldTask != null) _taskList.value = taskList.value - oldTask + updatedTask
        }

    }

    suspend fun deleteTask(task: Task) {
        // Call HTTP (opération longue):
        val tasksResponse = tasksWebService.delete(task)
        // À la ligne suivante, on a reçu la réponse de l'API:
        if (tasksResponse.isSuccessful) {
            val deletedTask = tasksResponse.body()
            // on modifie la valeur encapsulée, ce qui va notifier ses Observers et donc déclencher leur callback
            if (deletedTask != null) _taskList.value = taskList.value - deletedTask
        }
    }

    suspend fun createTask(task: Task) {
        // Call HTTP (opération longue):
        val tasksResponse = tasksWebService.create(task)
        // À la ligne suivante, on a reçu la réponse de l'API:
        if (tasksResponse.isSuccessful) {
            val newTask = tasksResponse.body()
            // on modifie la valeur encapsulée, ce qui va notifier ses Observers et donc déclencher leur callback
            if (newTask != null) _taskList.value = taskList.value + newTask
        }
    }
}