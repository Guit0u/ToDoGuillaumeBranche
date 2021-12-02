package com.example.guillaumebranche.todo.form

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.guillaumebranche.todo.R
import com.example.guillaumebranche.todo.tasklist.Task
import java.util.*

class FormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            val newTask = Task(id = UUID.randomUUID().toString(), title = "New Task !")
            intent.putExtra("task",newTask)
            setResult(RESULT_OK,intent)
            finish()
        }
    }
}