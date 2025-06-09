package com.example.proyecto

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale
import android.util.Log
import android.widget.Toast

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

        val publicacionId = intent.getStringExtra("publicacionId")
        if (publicacionId == null) {
            Log.e("Detalle", "No se recibió el ID de la publicación")
            Toast.makeText(this, "No se recibió el ID de la publicación", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("publicaciones")
            .document(publicacionId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    Log.d("Detalle", "Documento Firestore: ${document.data}")

                    val titulo = document.getString("titulo") ?: "Sin título"
                    val cuerpo = document.getString("cuerpo") ?: "Sin cuerpo"
                    val autorNombre = document.getString("autorNombre")
                    val urlImagen = document.getString("urlImagen")
                    val fecha = document.get("creadoEn")

                    findViewById<TextView>(R.id.tvTitulo)?.text = titulo
                    findViewById<TextView>(R.id.tvCuerpo)?.text = cuerpo

                    if (autorNombre.isNullOrEmpty()) {
                        findViewById<TextView>(R.id.footer)?.text = "Creado por: Desconocido"
                        Toast.makeText(this, "Campo 'autorNombre' vacío o no existe", Toast.LENGTH_SHORT).show()
                    } else {
                        findViewById<TextView>(R.id.footer)?.text = "Creado por: $autorNombre"
                    }

                    val fechaFormateada = when (fecha) {
                        is com.google.firebase.Timestamp -> {
                            val date = fecha.toDate()
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            dateFormat.format(date)
                        }
                        is Long -> {
                            val date = java.util.Date(fecha)
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            dateFormat.format(date)
                        }
                        is String -> fecha
                        else -> "Fecha no disponible"
                    }
                    findViewById<TextView>(R.id.tvFecha)?.text = fechaFormateada

                    val ivImagen = findViewById<ImageView>(R.id.ivImagen)
                    if (!urlImagen.isNullOrEmpty()) {
                        ivImagen.visibility = View.VISIBLE
                        Glide.with(this)
                            .load(urlImagen)
                            .into(ivImagen)
                    } else {
                        ivImagen.visibility = View.GONE
                        Toast.makeText(this, "Campo 'urlImagen' vacío o no existe", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("Detalle", "No existe la publicación")
                    Toast.makeText(this, "No existe la publicación", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("Detalle", "Error al obtener la publicación", e)
                Toast.makeText(this, "Error al obtener la publicación", Toast.LENGTH_SHORT).show()
            }
    }
}