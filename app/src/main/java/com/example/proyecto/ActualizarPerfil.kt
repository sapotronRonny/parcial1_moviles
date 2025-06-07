package com.example.proyecto

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class ActualizarPerfil : AppCompatActivity() {
    private lateinit var etNombre: EditText
    private lateinit var etGenero: EditText
    private lateinit var etNoticia: EditText
    private lateinit var etProvincia: EditText
    private lateinit var ivFoto: ImageView
    private var fotoBase64: String = ""
    private val PICK_IMAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_perfil)

        etNombre = findViewById(R.id.etNombrePerfil)
        etGenero = findViewById(R.id.etGeneroPerfil)
        etNoticia = findViewById(R.id.etNoticiaPerfil)
        etProvincia = findViewById(R.id.etProvinciaPerfil)
        ivFoto = findViewById(R.id.ivPerfilFoto)
        val btnActualizar = findViewById<Button>(R.id.btnActualizarPerfil)
        val btnCambiarFoto = findViewById<Button>(R.id.btnCambiarFoto)

        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()


        btnCambiarFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE)
        }

        btnActualizar.setOnClickListener {
            val nuevoNombre = etNombre.text.toString().trim()
            val nuevoGenero = etGenero.text.toString().trim()
            val nuevaNoticia = etNoticia.text.toString().trim()
            val nuevaProvincia = etProvincia.text.toString().trim()
            if (user != null) {
                db.collection("usuarios")
                    .whereEqualTo("correo", user.email)
                    .get()
                    .addOnSuccessListener { result ->
                        if (!result.isEmpty) {
                            val docId = result.documents[0].id
                            val updates = hashMapOf<String, Any>(
                                "nombre" to nuevoNombre,
                                "genero" to nuevoGenero,
                                "noticia" to nuevaNoticia,
                                "provincias" to nuevaProvincia,
                                "fotoBase64" to fotoBase64
                            )
                            db.collection("usuarios").document(docId)
                                .update(updates)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                        }
                    }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    ivFoto.setImageBitmap(bitmap)
                    // Convertir imagen a Base64
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    val byteArray = outputStream.toByteArray()
                    fotoBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}