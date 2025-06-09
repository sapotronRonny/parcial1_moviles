package com.example.proyecto

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import com.example.proyecto.model.Publicacion
import android.view.View
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale
import android.util.Log
import android.util.Base64
import android.graphics.BitmapFactory

class DetallePublicacionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle_publicacion)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        val publicacion = intent.getSerializableExtra("publicacion") as? Publicacion

        if (publicacion == null) {
            Log.e("Detalle", "No se recibió la publicación")
            return
        }

        Log.d("Detalle", "Publicacion recibida: $publicacion")

        findViewById<TextView>(R.id.tvTitulo)?.text = publicacion.titulo ?: "Sin título"
        findViewById<TextView>(R.id.tvCuerpo)?.text = publicacion.cuerpo ?: "Sin cuerpo"
        findViewById<TextView>(R.id.footer)?.text = "Creado por: ${publicacion.autorNombre ?: "Desconocido"}"

        val fechaFormateada = when (val fecha = publicacion.creadoEn) {
            is java.util.Date -> {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                dateFormat.format(fecha)
            }
            is Long -> {
                // Si es timestamp (milisegundos)
                val date = java.util.Date(fecha)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                dateFormat.format(date)
            }
            is String -> {
                try {
                    val fechaLimpia = fecha
                        .replace("\u202F", " ")
                        .replace(" ", " ")
                        .replace("a.m.", "AM")
                        .replace("p.m.", "PM")
                    // Cambia 'X' por 'Z' para compatibilidad con minSdk 23
                    val parser = SimpleDateFormat("d 'de' MMMM 'de' yyyy, h:mm:ss a 'UTC'Z", Locale("es", "MX"))
                    val date = parser.parse(fechaLimpia)
                    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    if (date != null) formatter.format(date) else fecha
                } catch (e: Exception) {
                    fecha // Si falla el parseo, muestra el string original
                }
            }
            else -> "Fecha no disponible"
        }
        findViewById<TextView>(R.id.tvFecha)?.text = fechaFormateada

        val ivImagen = findViewById<ImageView>(R.id.ivImagen)
        if (!publicacion.urlImagen.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(publicacion.urlImagen, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                ivImagen.setImageBitmap(bitmap)
                ivImagen.visibility = View.VISIBLE
            } catch (e: Exception) {
                ivImagen.visibility = View.GONE
            }
        } else {
            ivImagen.visibility = View.GONE
        }
    }
}