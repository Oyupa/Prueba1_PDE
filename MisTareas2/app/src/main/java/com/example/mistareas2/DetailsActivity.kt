package com.example.mistareas2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class DetailsActivity : ComponentActivity() {
    private lateinit var taskName: TextView
    private lateinit var taskDescription: TextView
    private lateinit var taskDate: TextView
    private lateinit var taskPriority: TextView
    private lateinit var taskCost: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // Inicializa las vistas
        taskName = findViewById(R.id.taskName)
        taskDescription = findViewById(R.id.taskDescription)
        taskDate = findViewById(R.id.taskDate)
        taskPriority = findViewById(R.id.taskPriority)
        taskCost = findViewById(R.id.taskCost)

        // Obtén los datos del intent y asigna los valores a las vistas
        val extras = intent.extras
        extras?.let {
            taskName.text = "Nombre: ${it.getString("task_name")}"
            taskDescription.text = "Descripción: ${it.getString("task_description")}"
            taskDate.text = "Fecha: ${it.getString("task_date")}"
            taskPriority.text = "Prioridad: ${it.getString("task_priority")}"
            taskCost.text = "Coste: ${it.getString("task_cost")}"
        }

        // Configura el botón para volver a MainActivity
        val btnBackToMain = findViewById<Button>(R.id.btnBackToMain)
        btnBackToMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
