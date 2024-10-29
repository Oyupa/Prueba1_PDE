package com.example.mistareas2

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class DetailsActivity : ComponentActivity() {
    private lateinit var taskName: TextView
    private lateinit var taskDescription: TextView
    private lateinit var taskDate: TextView
    private lateinit var taskPriority: TextView
    private lateinit var taskCost: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Configura el diseño de LinearLayout
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.VERTICAL

            taskName = TextView(this)
            taskDescription = TextView(this)
            taskDate = TextView(this)
            taskPriority = TextView(this)
            taskCost = TextView(this)

            linearLayout.addView(taskName)
            linearLayout.addView(taskDescription)
            linearLayout.addView(taskDate)
            linearLayout.addView(taskPriority)
            linearLayout.addView(taskCost)

            setContentView(linearLayout)
        }

        val extras = intent.extras
        extras?.let {
            taskName.text = "Nombre: ${it.getString("task_name")}"
            taskDescription.text = "Descripción: ${it.getString("task_description")}"
            taskDate.text = "Fecha: ${it.getString("task_date")}"
            taskPriority.text = "Prioridad: ${it.getString("task_priority")}"
            taskCost.text = "Coste: ${it.getString("task_cost")}"
        }
    }
}
