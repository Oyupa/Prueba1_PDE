package com.example.lista_de_tareas

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TareaAdapter(
    private val tareas: MutableList<Tarea>,
    private val onItemClicked: (Tarea) -> Unit
) : RecyclerView.Adapter<TareaAdapter.TareaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return TareaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = tareas[position]

        // Modifica el título si la tarea está completada
        val tituloConEstado = if (tarea.isCompletada) "${tarea.titulo} (Completada)" else tarea.titulo
        holder.bind(tituloConEstado)

        // Cambiar color de fondo según la posición (alternando verde y azul)
        val backgroundColor = if (position % 2 == 0) Color.parseColor("#CCFFCC") else Color.parseColor("#CCFFFF")
        holder.itemView.setBackgroundColor(backgroundColor)

        holder.itemView.setOnClickListener {
            onItemClicked(tarea) // Llamada cuando se hace clic en una tarea
        }
    }

    override fun getItemCount(): Int = tareas.size

    class TareaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tareaTitle: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(tituloConEstado: String) {
            tareaTitle.text = tituloConEstado
        }
    }
}
