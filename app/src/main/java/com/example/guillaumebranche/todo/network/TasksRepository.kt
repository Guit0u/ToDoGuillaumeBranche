package com.example.guillaumebranche.todo.network

import com.example.guillaumebranche.todo.tasklist.Task

class TasksRepository {
    private val tasksWebService = Api.tasksWebService

    suspend fun loadTasks(): List<Task>? {
        val response = tasksWebService.getTasks()
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun updateTask(task: Task) {
        tasksWebService.update(task)
    }

    suspend fun deleteTask(task: Task) {
        tasksWebService.delete(task.id)
    }

    suspend fun createTask(task: Task) {
        tasksWebService.create(task)
    }
}