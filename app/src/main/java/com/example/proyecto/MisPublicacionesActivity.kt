package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.model.Publicacion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MisPublicacionesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_publicaciones)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMisPublicaciones)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        FirebaseFirestore.getInstance()
            .collection("publicaciones")
            .whereEqualTo("idUsuario", userId)
            .get()
            .addOnSuccessListener { result ->
                val publicaciones = result.map { doc ->
                    val data = doc.data
                    val creadoEn = (data["creadoEn"] as? com.google.firebase.Timestamp)?.toDate()?.time ?: 0L
                    Publicacion(
                        id = doc.id,
                        idUsuario = data["idUsuario"] as? String ?: "",
                        titulo = data["titulo"] as? String ?: "",
                        cuerpo = data["cuerpo"] as? String ?: "",
                        urlImagen = data["urlImagen"] as? String ?: "",
                        creadoEn = creadoEn
                    )
                }
                val adapter = MisPublicacionesAdapter(publicaciones,
                    onEditar = { publicacion ->
                        val intent = Intent(this, EditarPublicacionActivity::class.java)
                        intent.putExtra("publicacion", publicacion)
                        startActivity(intent)
                    },
                    onEliminar = { publicacion, onSuccess ->
                        FirebaseFirestore.getInstance()
                            .collection("publicaciones")
                            .document(publicacion.id)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Publicación eliminada", Toast.LENGTH_SHORT).show()
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                )
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}