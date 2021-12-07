package com.example.guillaumebranche.todo.tasklist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Task(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    var title: String,
    @SerialName("description")
    var description: String = "Ceci est une tâche",

    ) : java.io.Serializable
