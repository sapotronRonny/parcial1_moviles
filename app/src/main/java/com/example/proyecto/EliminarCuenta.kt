package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EliminarCuenta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eliminar_cuenta)

        val btnConfirmar = findViewById<Button>(R.id.btnConfirmarEliminar)
        val etPassword = findViewById<EditText>(R.id.etPasswordEliminar)
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        btnConfirmar.setOnClickListener {
            val password = etPassword.text.toString()
            if (user != null && user.email != null && password.isNotEmpty()) {
                // Reautenticación
                val credential = EmailAuthProvider.getCredential(user.email!!, password)
                user.reauthenticate(credential)
                    .addOnSuccessListener {
                        // Elimina datos en Firestore
                        db.collection("usuarios")
                            .whereEqualTo("correo", user.email)
                            .get()
                            .addOnSuccessListener { result ->
                                for (doc in result.documents) {
                                    db.collection("usuarios").document(doc.id).delete()
                                }
                                // Elimina usuario de Auth
                                user.delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Cuenta eliminada", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, RegisterActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Error al eliminar la cuenta: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Contraseña incorrecta o sesión expirada", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show()
            }
        }
    }
}