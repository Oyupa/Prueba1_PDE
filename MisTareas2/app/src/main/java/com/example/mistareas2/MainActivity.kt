package com.example.mistareas2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : ComponentActivity() {
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskRecyclerView = findViewById(R.id.taskRecyclerView)
        taskAdapter = TaskAdapter(taskList) { task -> openTaskDetails(task) }
        taskRecyclerView.adapter = taskAdapter
        taskRecyclerView.layoutManager = LinearLayoutManager(this)

        sharedPreferences = getSharedPreferences("tasks", MODE_PRIVATE)
        loadTasks()

        findViewById<Button>(R.id.addButton).setOnClickListener {
            val intent = Intent(this, RegistreTaskActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_TASK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_ADD_TASK && resultCode == Activity.RESULT_OK && data != null) {
            val name = data.getStringExtra("task_name") ?: ""
            val description = data.getStringExtra("task_description") ?: ""
            val date = data.getStringExtra("task_date") ?: ""
            val priority = data.getStringExtra("task_priority") ?: ""
            val cost = data.getStringExtra("task_cost") ?: ""

            val newTask = Task(name, description, date, priority, cost)
            taskList.add(newTask)
            taskAdapter.notifyItemInserted(taskList.size - 1)
            saveTasks()
        }
    }

    private fun openTaskDetails(task: Task) {
        val intent = Intent(this, DetailsActivity::class.java).apply {
            putExtra("task_name", task.name)
            putExtra("task_description", task.description)
            putExtra("task_date", task.date)
            putExtra("task_priority", task.priority)
            putExtra("task_cost", task.cost)
        }
        startActivity(intent)
    }

    private fun saveTasks() {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(taskList)
        editor.putString("task_list", json)
        editor.apply()
    }

    private fun loadTasks() {
        val gson = Gson()
        val json = sharedPreferences.getString("task_list", null)
        val type = object : TypeToken<MutableList<Task>>() {}.type
        val savedTasks: MutableList<Task>? = gson.fromJson(json, type)
        savedTasks?.let { taskList.addAll(it) }
    }

    companion object {
        private const val REQUEST_CODE_ADD_TASK = 1
    }
}
