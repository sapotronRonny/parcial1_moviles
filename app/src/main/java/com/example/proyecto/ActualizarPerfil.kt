package com.example.proyecto

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ActualizarPerfil : AppCompatActivity() {
    private lateinit var etNombre: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_perfil)

        etNombre = findViewById(R.id.etNombrePerfil)
        val btnActualizar = findViewById<Button>(R.id.btnActualizarPerfil)

        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        // Mostrar el nombre actual
        if (user != null) {
            db.collection("usuarios")
                .whereEqualTo("correo", user.email)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val nombreActual = result.documents[0].getString("nombre")
                        etNombre.setText(nombreActual)
                    }
                }
        }

        btnActualizar.setOnClickListener {
            val nuevoNombre = etNombre.text.toString().trim()
            if (user != null) {
                db.collection("usuarios")
                    .whereEqualTo("correo", user.email)
                    .get()
                    .addOnSuccessListener { result ->
                        if (!result.isEmpty) {
                            val docId = result.documents[0].id
                            val updates = hashMapOf<String, Any>(
                                "nombre" to nuevoNombre
                            )
                            db.collection("usuarios").document(docId)
                                .update(updates)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Nombre actualizado", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                        }
                    }
            }
        }
    }
}