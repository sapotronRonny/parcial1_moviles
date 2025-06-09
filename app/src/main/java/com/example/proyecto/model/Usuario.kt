package com.example.proyecto.model

data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val correo: String = "",
    val genero: String = "",
    val noticia: String = "",
    val provincias: String = "",
    val fotoBase64: String? = null
)
