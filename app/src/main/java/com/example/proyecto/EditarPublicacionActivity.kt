package com.example.proyecto

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.model.Publicacion
import com.google.firebase.firestore.FirebaseFirestore

class EditarPublicacionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_publicacion)

        val publicacion = intent.getSerializableExtra("publicacion") as? Publicacion
        if (publicacion == null) {
            Toast.makeText(this, "Error al recibir la publicación", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val etTitulo = findViewById<EditText>(R.id.etTitulo1)
        val etCuerpo = findViewById<EditText>(R.id.etCuerpo1)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar1)

        etTitulo.setText(publicacion.titulo)
        etCuerpo.setText(publicacion.cuerpo)

        btnGuardar.setOnClickListener {
            val nuevoTitulo = etTitulo.text.toString()
            val nuevoCuerpo = etCuerpo.text.toString()
            val docId = publicacion.id

            if (docId.isNullOrEmpty()) {
                Toast.makeText(this, "ID de publicación inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseFirestore.getInstance()
                .collection("publicaciones")
                .document(docId)
                .update(mapOf("titulo" to nuevoTitulo, "cuerpo" to nuevoCuerpo))
                .addOnSuccessListener {
                    Toast.makeText(this, "Publicación actualizada", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}