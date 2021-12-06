package com.example.guillaumebranche.todo.tasklist


interface TaskListListener {
    fun onClickDelete(task: Task)
    fun onClickModify(task : Task)
    fun onClickShare(task : Task) : Boolean
}