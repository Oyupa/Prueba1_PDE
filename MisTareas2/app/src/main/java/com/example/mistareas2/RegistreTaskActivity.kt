package com.example.mistareas2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity

class RegistreTaskActivity : ComponentActivity() {
    private lateinit var taskName: EditText
    private lateinit var taskDescription: EditText
    private lateinit var taskDate: EditText
    private lateinit var taskPriority: EditText
    private lateinit var taskCost: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registre_task)

        taskName = findViewById(R.id.taskName)
        taskDescription = findViewById(R.id.taskDescription)
        taskDate = findViewById(R.id.taskDate)
        taskPriority = findViewById(R.id.taskPriority)
        taskCost = findViewById(R.id.taskCost)

        findViewById<Button>(R.id.addButton).setOnClickListener {
            addTask()
        }

        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    private fun addTask() {
        val name = taskName.text.toString()
        val description = taskDescription.text.toString()
        val date = taskDate.text.toString()
        val priority = taskPriority.text.toString()
        val cost = taskCost.text.toString()

        val intent = Intent()
        intent.putExtra("task_name", name)
        intent.putExtra("task_description", description)
        intent.putExtra("task_date", date)
        intent.putExtra("task_priority", priority)
        intent.putExtra("task_cost", cost)

        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
