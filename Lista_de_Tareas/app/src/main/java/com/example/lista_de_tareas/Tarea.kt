package com.example.lista_de_tareas

data class Tarea(
    val titulo: String,
    var isCompletada: Boolean = false
) {
    constructor() : this( "", false)
}
