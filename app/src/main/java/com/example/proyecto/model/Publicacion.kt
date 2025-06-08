package com.example.proyecto.model

data class Publicacion(
    val id: String = "",
    val idUsuario: String = "",
    val titulo: String = "",
    val cuerpo: String = "",
    val urlImagen: String = "",
//    val timestamp: Long = System.currentTimeMillis()
    val creadoEn: com.google.firebase.Timestamp? = null
)
