package com.example.lista_de_tareas

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {

    private val db: FirebaseFirestore = Firebase.firestore // Inicialización de Firestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var tareaList: MutableList<Tarea> // Lista mutable para almacenar las tareas
    private lateinit var adapter: TareaAdapter // Adaptador para el RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa la lista de tareas
        tareaList = mutableListOf()

        // Configura el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewTasks)
        recyclerView.layoutManager = LinearLayoutManager(this) // Usa LinearLayoutManager
        adapter = TareaAdapter(tareaList) { tarea ->
            // Manejar el clic en la tarea (por ejemplo, marcarla como completada o eliminarla)
            showOptionsDialog(tarea)
            Toast.makeText(this, "Tarea seleccionada: ${tarea.titulo}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        // Carga las tareas de Firestore
        loadTareas()

        // Configura el botón de agregar tarea
        val addButton: Button = findViewById(R.id.buttonAddTask)
        addButton.setOnClickListener {
            addNewTask()
        }
        /*
        val showPending: Button = findViewById(R.id.buttonShowPending)
        addButton.setOnClickListener {
            showPendingTasks()
        }

        val showCompleted: Button = findViewById(R.id.buttonShowCompleted)
        addButton.setOnClickListener {
            showCompletedTasks()
        }

         */
    }

    private fun loadTareas() {
        db.collection("Tareas")
            .get()
            .addOnSuccessListener { result ->
                tareaList.clear() // Limpia la lista antes de agregar nuevas tareas
                for (document in result) {
                    val tarea = document.toObject(Tarea::class.java) // Convierte el documento a la clase Tarea
                    Log.d("Tarea", "Título: ${tarea.titulo}, Completada: ${tarea.isCompletada}")
                    tareaList.add(tarea) // Agrega la tarea a la lista
                }
                adapter.notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado
                Toast.makeText(this, "Tareas cargadas: ${tareaList.size}", Toast.LENGTH_SHORT).show() // Para depuración
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar tareas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addNewTask() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Agregar Nueva Tarea")

        // Crear un layout para el formulario
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 20, 50, 20)

        val titleInput = EditText(this)
        titleInput.hint = "Título de la tarea"
        layout.addView(titleInput)

        dialogBuilder.setView(layout)

        dialogBuilder.setPositiveButton("Agregar") { _, _ ->
            val title = titleInput.text.toString()
            if (title.isNotEmpty()) {
                val nuevaTarea = Tarea(title, false)
                // Guardar la tarea en Firestore
                db.collection("Tareas").add(nuevaTarea)
                    .addOnSuccessListener {
                        tareaList.add(nuevaTarea) // Añadir la tarea a la lista local
                        adapter.notifyItemInserted(tareaList.size - 1) // Notifica que se ha añadido un nuevo ítem
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al agregar tarea: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        dialogBuilder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        dialogBuilder.show()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.add(0, 1, 0, "Marcar como Hecha")
        menu.add(0, 2, 1, "Eliminar")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        // Aquí se debe manejar el clic en el contexto
        // Debes agregar la lógica para identificar qué tarea se ha seleccionado en el RecyclerView
        return super.onContextItemSelected(item)
    }



    private fun deleteTask(tarea: Tarea) {
        tareaList.remove(tarea) // Elimina la tarea de la lista local
        adapter.notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado
        db.collection("Tareas").document(tarea.titulo)
            .delete()
            .addOnSuccessListener {
                tareaList.remove(tarea)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar tarea: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            saveTareas()
    }

    private fun showOptionsDialog(tarea: Tarea) {
        val options = arrayOf("Borrar", if (tarea.isCompletada) "Descompletar" else "Completar")
        AlertDialog.Builder(this)
            .setTitle("Opciones para la tarea")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> deleteTask(tarea)
                    1 -> toggleTaskCompletion(tarea)
                }
            }
            .show()
    }

    private fun toggleTaskCompletion(tarea: Tarea) {
        tarea.isCompletada = !tarea.isCompletada
        adapter.notifyDataSetChanged()
        db.collection("Tareas").document(tarea.titulo)
            .set(tarea)
            .addOnSuccessListener {
                Toast.makeText(this, "Tarea actualizada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al actualizar tarea: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        saveTareas()
    }
    private fun saveTareas() {
        // Guarda las tareas en Firestore
        for (tarea in tareaList) {
            db.collection("Tareas").document(tarea.titulo).set(tarea)
                .addOnSuccessListener {
                    Log.d("Tarea", "Tarea guardada: ${tarea.titulo}")
                }
                .addOnFailureListener { e ->
                    Log.e("Tarea", "Error al guardar tarea: ${e.message}")
                }
        }
    }
    /*
    private fun showPendingTasks() {
        val pendingTasks = tareaList.filter { !it.isCompletada } // Filtra las tareas pendientes
        tareaList.clear()
        tareaList.addAll(pendingTasks) // Actualiza la lista principal
        adapter.notifyDataSetChanged() // Notifica cambios al adaptador
    }

    private fun showCompletedTasks() {
        val completedTasks = tareaList.filter { it.isCompletada } // Filtra las tareas completadas
        tareaList.clear()
        tareaList.addAll(completedTasks) // Actualiza la lista principal
        adapter.notifyDataSetChanged() // Notifica cambios al adaptador
    }

     */

}
