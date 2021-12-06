package com.example.guillaumebranche.todo.tasklist

import java.io.Serializable

data class Task(
    val id: String,
    var title: String,
    var description: String = "Ceci est une tâche",

    ) : Serializable{
}
