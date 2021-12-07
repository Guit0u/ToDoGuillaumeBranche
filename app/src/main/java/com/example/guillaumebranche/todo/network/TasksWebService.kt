package com.example.guillaumebranche.todo.network

import com.example.guillaumebranche.todo.tasklist.Task
import retrofit2.Response
import retrofit2.http.*

interface TasksWebService {
    @GET("tasks")
    suspend fun getTasks(): Response<List<Task>>

    @POST("tasks")
    suspend fun create(@Body task: Task): Response<Task>

    @PATCH("tasks/{id}")
    suspend fun update(@Body task: Task, @Path("id") id: String? = task.id): Response<Task>

    @DELETE("tasks/{id}")
    suspend fun delete(@Body task: Task, @Path("id") id: String? = task.id): Response<Task>

    //@PUT("tasks/{id}/mark_as_done")
    //suspend fun put(@Body task: Task, @Path("id") id: String? = task.id): Response<Task>

}