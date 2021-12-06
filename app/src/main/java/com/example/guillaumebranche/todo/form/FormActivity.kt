package com.example.guillaumebranche.todo.form

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.guillaumebranche.todo.R
import com.example.guillaumebranche.todo.tasklist.Task
import java.util.*

class FormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        val title = findViewById<EditText>(R.id.editTitle)
        val desc = findViewById<EditText>(R.id.editDescription)
        title.hint = "Title"
        desc.hint = "Description"

        val task = intent.getSerializableExtra("task") as? Task
        title.setText(task?.title)
        desc.setText(task?.description)

        val id = task?.id ?: UUID.randomUUID().toString()

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{

            val newTask = Task(
                    id = id,
                    title = title.text.toString(),
                    description = desc.text.toString()
            )
            intent.putExtra("task", newTask)
            setResult(RESULT_OK,intent)
            finish()
        }
    }
}