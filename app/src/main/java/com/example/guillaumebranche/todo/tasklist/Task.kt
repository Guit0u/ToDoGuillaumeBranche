package com.example.guillaumebranche.todo.tasklist

import java.io.Serializable

data class Task(
    val id: String,
    val title: String,
    val description: String = "Ceci est une tâche",

) : Serializable{
}
