package com.example.proyecto

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.util.Base64
import java.io.ByteArrayOutputStream
import com.google.firebase.firestore.FirebaseFirestore
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import android.graphics.BitmapFactory
import com.google.firebase.Timestamp


class CrearpubliActivity : AppCompatActivity() {

    private lateinit var etTitulo: EditText
    private lateinit var etCuerpo: EditText
    private lateinit var ivPreview: ImageView
    private var imagenUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crearpubli)

        etTitulo = findViewById(R.id.etTitulo)
        etCuerpo = findViewById(R.id.etCuerpo)
        ivPreview = findViewById(R.id.ivPreview)

        findViewById<Button>(R.id.btnAgregarImagen).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        findViewById<Button>(R.id.btnSubirPublicacion).setOnClickListener {
            subirPublicacion()
        }

    }

//funciones a partir de aqui

    private fun uriToBase64(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            var bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Reescalar si el ancho es mayor a 1280px
            val maxWidth = 1280
            if (bitmap.width > maxWidth) {
                val aspectRatio = bitmap.height.toDouble() / bitmap.width
                val newHeight = (maxWidth * aspectRatio).toInt()
                bitmap = Bitmap.createScaledBitmap(bitmap, maxWidth, newHeight, true)
            }

            // Comprimir hasta que pese menos de 1MB
            var quality = 90
            var byteArray: ByteArray
            do {
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                byteArray = outputStream.toByteArray()
                quality -= 10
            } while (byteArray.size > 1024 * 1024 && quality > 10)

            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imagenUri = data?.data
            ivPreview.setImageURI(imagenUri)
        }
    }

    private fun subirPublicacion() {
        val titulo = etTitulo.text.toString().trim()
        val cuerpo = etCuerpo.text.toString().trim()
        val usuarioActualId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        if (titulo.isEmpty() || cuerpo.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val base64Imagen = imagenUri?.let { uriToBase64(it) } ?: ""

        guardarEnFirestore(titulo, cuerpo, base64Imagen, usuarioActualId)
    }

    private fun guardarEnFirestore(titulo: String, cuerpo: String, urlImagen: String, usuarioActualId: String) {
        val db = FirebaseFirestore.getInstance()
        val id = db.collection("publicaciones").document().id
        val publicacion = hashMapOf(
            "id" to id,
            "idUsuario" to usuarioActualId,
            "titulo" to titulo,
            "cuerpo" to cuerpo,
            "urlImagen" to urlImagen,
            "creadoEn" to Timestamp.now()
        )
        db.collection("publicaciones").document(id).set(publicacion)
            .addOnSuccessListener {
                Toast.makeText(this, "Publicación subida", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {

                Toast.makeText(this, "Error al subir publicación: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}