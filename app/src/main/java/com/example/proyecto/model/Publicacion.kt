package com.example.proyecto.model

import java.io.Serializable

data class Publicacion(
    val id: String = "",
    val idUsuario: String = "",
    val autorNombre: String = "",
    val titulo: String = "",
    val cuerpo: String = "",
    val urlImagen: String = "",
    val creadoEn: Long = 0L // O el tipo que uses
): Serializable
//    val timestamp: Long = System.currentTimeMillis()
//    val creadoEn: com.google.firebase.Timestamp? = null