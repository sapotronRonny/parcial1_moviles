package com.example.proyecto

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.BitmapFactory
import android.util.Base64

class PerfilActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val tvPerfil = findViewById<TextView>(R.id.tvPerfilUsuario)
        val ivFoto = findViewById<ImageView>(R.id.ivPerfilFoto)
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("usuarios")
                .whereEqualTo("correo", user.email)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val doc = result.documents[0]
                        val nombre = doc.getString("nombre") ?: ""
                        val correo = doc.getString("correo") ?: ""
                        val genero = doc.getString("genero") ?: ""
                        val noticia = doc.getString("noticia") ?: ""
                        val provincias = doc.getString("provincias") ?: ""
                        val fotoBase64 = doc.getString("fotoBase64") ?: ""

                        tvPerfil.text = "$nombre - $correo\n$genero | $noticia | $provincias"

                        if (fotoBase64.isNotEmpty()) {
                            try {
                                val imageBytes = Base64.decode(fotoBase64, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                ivFoto.setImageBitmap(bitmap)
                            } catch (e: Exception) {
                                Toast.makeText(this, "Error al cargar la foto", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        tvPerfil.text = "No se encontraron datos del usuario"
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            tvPerfil.text = "Usuario no autenticado"
        }
    }
}