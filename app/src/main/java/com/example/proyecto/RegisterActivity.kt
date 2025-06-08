package com.example.proyecto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.proyecto.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import android.content.pm.PackageManager
import android.Manifest
import android.view.View


class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContraseña: EditText
    private lateinit var rgGenero: RadioGroup
    private lateinit var checkPolitica: CheckBox
    private lateinit var checkDeportes: CheckBox
    private lateinit var checkCultura: CheckBox
    private lateinit var checkEducacion: CheckBox
    private lateinit var checkSalud: CheckBox
    private lateinit var spnProvincias: Spinner
    private lateinit var btnRegistro: Button
    private lateinit var cardResultado: CardView
    private lateinit var tvResultado: TextView
    private lateinit var btnLimpiar: Button
    private lateinit var ivFoto: ImageView
    private var imageUri: Uri? = null
    private val REQUEST_PERMISSIONS_CODE = 100
    private var imageBase64: String? = null
    private lateinit var btnEliminarFoto: ImageButton

    companion object {
        private const val KEY_IMAGE_URI = "key_image_uri"
    }

    private val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
            ivFoto.setImageBitmap(bitmap)
            imageBase64 = bitmapToBase64(bitmap)
            btnEliminarFoto.visibility = View.VISIBLE
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            ivFoto.setImageBitmap(it)
            imageBase64 = bitmapToBase64(it)
            btnEliminarFoto.visibility = View.VISIBLE
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.registro)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        etContraseña = findViewById(R.id.etContraseña)
        rgGenero = findViewById(R.id.rgGenero)
        checkPolitica = findViewById(R.id.chkPolitica)
        checkDeportes = findViewById(R.id.chkDeportes)
        checkCultura = findViewById(R.id.chkCultura)
        checkEducacion = findViewById(R.id.chkEducacion)
        checkSalud = findViewById(R.id.chkSalud)
        spnProvincias = findViewById(R.id.spnProvincias)
        btnRegistro = findViewById(R.id.btnRegistro)
        cardResultado = findViewById(R.id.cardResultado)
        tvResultado = findViewById(R.id.tvResultado)
        btnLimpiar = findViewById(R.id.btnLimpiar)
        ivFoto = findViewById(R.id.ivFoto)
        btnEliminarFoto = findViewById(R.id.btnEliminarFoto)


        val provincias = arrayOf(
            "Bolívar", "Chone", "El Carmen", "Flavio Alfaro", "Jama", "Jaramijó",
            "Jipijapa", "Junín", "Manta", "Montecristi", "Olmedo", "Paján",
            "Pedernales", "Pichincha", "Portoviejo", "Puerto López", "Rocafuerte",
            "Santa Ana", "Sucre", "Tosagua", "24 de Mayo"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, provincias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnProvincias.adapter = adapter

        btnRegistro.setOnClickListener { enviarDatos() }
        btnLimpiar.setOnClickListener { limpiardatos() }

        val btnEliminarFoto = findViewById<ImageButton>(R.id.btnEliminarFoto)
        val ivFoto = findViewById<ImageView>(R.id.ivFoto)

        btnEliminarFoto.setOnClickListener {
            ivFoto.setImageResource(0)
            imageBase64 = null
            btnEliminarFoto.visibility = View.GONE
        }



        // Configurar lanzadores para tomar y cargar fotos
        findViewById<Button>(R.id.btnTomarFoto).setOnClickListener {
            takePictureLauncher.launch(null)
        }
        findViewById<Button>(R.id.btnCargarFoto).setOnClickListener {
            pickPhotoLauncher.launch("image/*")
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        imageUri?.let {
            outState.putString(KEY_IMAGE_URI, it.toString())
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        // Reescalar si el ancho es mayor a 1280px
        val maxWidth = 1280
        var scaledBitmap = bitmap
        if (bitmap.width > maxWidth) {
            val aspectRatio = bitmap.height.toDouble() / bitmap.width
            val newHeight = (maxWidth * aspectRatio).toInt()
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, maxWidth, newHeight, true)
        }

        var quality = 90
        var byteArray: ByteArray
        do {
            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            byteArray = outputStream.toByteArray()
            quality -= 10
        } while (byteArray.size > 1024 * 1024 && quality > 10)

        // Si aún así pesa más de 1MB, reescalar más pequeño y volver a intentar
        while (byteArray.size > 1024 * 1024 && scaledBitmap.width > 640) {
            val newWidth = scaledBitmap.width / 2
            val newHeight = scaledBitmap.height / 2
            scaledBitmap = Bitmap.createScaledBitmap(scaledBitmap, newWidth, newHeight, true)
            quality = 90
            do {
                val outputStream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                byteArray = outputStream.toByteArray()
                quality -= 10
            } while (byteArray.size > 1024 * 1024 && quality > 10)
        }

        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun enviarDatos() {
        val nombre = etNombre.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val contraseña = etContraseña.text.toString().trim()

        // Validaciones
        if (nombre.isEmpty() || nombre.length < 8 || nombre.any { it.isDigit() }) {
            etNombre.error = "Nombre inválido"
            return
        }

        if (correo.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.error = "Correo inválido"
            return
        }

        if (contraseña.length < 8 || !contraseña.any { it.isDigit() } || !contraseña.any { it.isUpperCase() }) {
            etContraseña.error = "Contraseña débil"
            return
        }

        val idGeneroSeleccionado = rgGenero.checkedRadioButtonId
        if (idGeneroSeleccionado == -1) {
            Toast.makeText(this, "Selecciona un género", Toast.LENGTH_SHORT).show()
            return
        }

        val genero = findViewById<RadioButton>(idGeneroSeleccionado).text.toString()
        val provincia = spnProvincias.selectedItem.toString()

        val noticia = mutableListOf<String>().apply {
            if (checkDeportes.isChecked) add("Deportes")
            if (checkCultura.isChecked) add("Cultura")
            if (checkEducacion.isChecked) add("Educación")
            if (checkSalud.isChecked) add("Salud")
            if (checkPolitica.isChecked) add("Política")
        }

        auth.createUserWithEmailAndPassword(correo, contraseña)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                    // Construir el mapa de usuario
                    val usuario = hashMapOf(
                        "nombre" to nombre,
                        "correo" to correo,
                        "genero" to genero,
                        "noticia" to noticia.joinToString(", "),
                        "provincias" to provincia
                    )
                    imageBase64?.let { usuario["fotoBase64"] = it }

                    db.collection("usuarios").document(userId)
                        .set(usuario)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show()
                            auth.signOut()
                            irHome()
                        }
                        .addOnFailureListener {
                            auth.currentUser?.delete()
                            Toast.makeText(this, "Error al guardar datos: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(this, "Error al crear usuario: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }


    }

    private fun limpiardatos() {
        etNombre.text.clear()
        etCorreo.text.clear()
        etContraseña.text.clear()
        rgGenero.clearCheck()
        checkDeportes.isChecked = false
        checkPolitica.isChecked = false
        checkSalud.isChecked = false
        checkCultura.isChecked = false
        checkEducacion.isChecked = false
        spnProvincias.setSelection(0)
        ivFoto.setImageResource(0)
        imageUri = null
        cardResultado.visibility = android.view.View.GONE
    }

    private fun irHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
















//package com.example.proyecto
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.graphics.Bitmap
//import android.net.Uri
//import android.os.Bundle
//import android.provider.MediaStore
//import android.view.View
//import android.widget.*
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.cardview.widget.CardView
//import com.example.proyecto.model.Usuario
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.storage.FirebaseStorage
//import java.util.UUID
//
//class RegisterActivity : AppCompatActivity() {
//
//    private lateinit var auth: FirebaseAuth
//    private lateinit var db: FirebaseFirestore
//    private lateinit var etNombre: EditText
//    private lateinit var etCorreo: EditText
//    private lateinit var etContraseña: EditText
//    private lateinit var rgGenero: RadioGroup
//    private lateinit var checkPolitica: CheckBox
//    private lateinit var checkDeportes: CheckBox
//    private lateinit var checkCultura: CheckBox
//    private lateinit var checkEducacion: CheckBox
//    private lateinit var checkSalud: CheckBox
//    private lateinit var spnProvincias: Spinner
//    private lateinit var btnRegistro: Button
//    private lateinit var cardResultado: CardView
//    private lateinit var tvResultado: TextView
//    private lateinit var btnLimpiar: Button
//    private lateinit var ivFoto: ImageView
//    private val REQUEST_IMAGE_CAPTURE = 1
//    private val REQUEST_IMAGE_PICK = 2
//    private var imageUri: Uri? = null
//
//
//
//    companion object {
//        private const val KEY_IMAGE_URI = "key_image_uri"
//    }
//
//    private val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        uri?.let {
//            ivFoto.setImageURI(it)
//            imageUri = it
//        }
//    }
//
//    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
//        bitmap?.let {
//            ivFoto.setImageBitmap(it)
//            val path = MediaStore.Images.Media.insertImage(contentResolver, it, "FotoPerfil", null)
//            imageUri = Uri.parse(path)
//        }
//    }
//
//    @SuppressLint("MissingInflatedId")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        auth = FirebaseAuth.getInstance()
//        db = FirebaseFirestore.getInstance()
//        setContentView(R.layout.registro)
//        ivFoto = findViewById(R.id.ivFoto)
//
//
//        etNombre = findViewById(R.id.etNombre)
//        etCorreo = findViewById(R.id.etCorreo)
//        etContraseña = findViewById(R.id.etContraseña)
//        rgGenero = findViewById(R.id.rgGenero)
//        checkPolitica = findViewById(R.id.chkPolitica)
//        checkDeportes = findViewById(R.id.chkDeportes)
//        checkCultura = findViewById(R.id.chkCultura)
//        checkEducacion = findViewById(R.id.chkEducacion)
//        checkSalud = findViewById(R.id.chkSalud)
//        spnProvincias = findViewById(R.id.spnProvincias)
//        btnRegistro = findViewById(R.id.btnRegistro)
//        cardResultado = findViewById(R.id.cardResultado)
//        tvResultado = findViewById(R.id.tvResultado)
//        btnLimpiar = findViewById(R.id.btnLimpiar)
//
//        if (savedInstanceState != null) {
//            val savedUriString = savedInstanceState.getString(KEY_IMAGE_URI)
//            if (savedUriString != null) {
//                imageUri = Uri.parse(savedUriString)
//                ivFoto.setImageURI(imageUri) // Actualiza la UI si la URI fue restaurada
//            }
//        }
//
//
//        val provincias = arrayOf(
//            "Bolívar", "Chone", "El Carmen", "Flavio Alfaro", "Jama", "Jaramijó",
//            "Jipijapa", "Junín", "Manta", "Montecristi", "Olmedo", "Paján",
//            "Pedernales", "Pichincha", "Portoviejo", "Puerto López", "Rocafuerte",
//            "Santa Ana", "Sucre", "Tosagua", "24 de Mayo"
//        )
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, provincias)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spnProvincias.adapter = adapter
//
//        btnRegistro.setOnClickListener {
//            enviarDatos()
//        }
//
//        btnLimpiar.setOnClickListener {
//            limpiardatos()
//        }
//
//        findViewById<Button>(R.id.btnTomarFoto).setOnClickListener {
//            takePictureLauncher.launch(null)
//        }
//
//        findViewById<Button>(R.id.btnCargarFoto).setOnClickListener {
//            pickPhotoLauncher.launch("image/*")
//        }
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        imageUri?.let {
//            outState.putString(KEY_IMAGE_URI, it.toString())
//        }
//    }
//
//
//
//    private fun enviarDatos() {
//        val nombre = etNombre.text.toString().trim()
//        val correo = etCorreo.text.toString().trim()
//        val contraseña = etContraseña.text.toString().trim()
//
//        // Validaciones básicas
//        if (nombre.isEmpty() || nombre.length < 8 || nombre.any { it.isDigit() }) {
//            etNombre.error = "Nombre inválido"
//            return
//        }
//
//        if (correo.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
//            etCorreo.error = "Correo inválido"
//            return
//        }
//
//        if (contraseña.length < 8 || !contraseña.any { it.isDigit() } || !contraseña.any { it.isUpperCase() }) {
//            etContraseña.error = "Contraseña débil"
//            return
//        }
//
//        val idGeneroSeleccionado = rgGenero.checkedRadioButtonId
//        if (idGeneroSeleccionado == -1) {
//            Toast.makeText(this, "Selecciona un género", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val genero = findViewById<RadioButton>(idGeneroSeleccionado).text.toString()
//        val provincia = spnProvincias.selectedItem.toString()
//
//        val noticia = mutableListOf<String>().apply {
//            if (checkDeportes.isChecked) add("Deportes")
//            if (checkCultura.isChecked) add("Cultura")
//            if (checkEducacion.isChecked) add("Educación")
//            if (checkSalud.isChecked) add("Salud")
//            if (checkPolitica.isChecked) add("Política")
//        }
//
//        // Aquí chequeamos si la imagen está seleccionada
//        if (imageUri == null) {
//            Toast.makeText(this, "Por favor selecciona una foto", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // 1. Crear usuario con correo y contraseña en Firebase Auth
//        auth.createUserWithEmailAndPassword(correo, contraseña)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
//
//                    // 2. Subir la foto a Firebase Storage
//                    val storageRef = FirebaseStorage.getInstance().reference
//                    val fileRef = storageRef.child("fotos_usuarios/${UUID.randomUUID()}.jpg")
//
//
//
//                    fileRef.putFile(imageUri!!)
//                        .addOnSuccessListener {
//                            // 3. Obtener URL de descarga de la imagen
//                            fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
//                                val fotoUrl = downloadUri.toString()
//
//                                // 4. Crear mapa con datos completos, incluyendo URL de la foto
//                                val datosUsuario = hashMapOf(
//                                    "nombre" to nombre,
//                                    "correo" to correo,
//                                    "genero" to genero,
//                                    "provincia" to provincia,
//                                    "noticia" to noticia,
//                                    "fotoUrl" to fotoUrl
//                                )
//
//                                // 5. Guardar datos en Firestore usando el ID del usuario
//                                db.collection("usuarios").document(userId)
//                                    .set(datosUsuario)
//                                    .addOnSuccessListener {
//                                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show()
//                                        irHome() // Navegar a la pantalla principal, si tienes esta función
//                                    }
//                                    .addOnFailureListener {
//                                        Toast.makeText(this, "Error al guardar datos: ${it.message}", Toast.LENGTH_LONG).show()
//                                    }
//                            }.addOnFailureListener {
//                                Toast.makeText(this, "Error al obtener URL de la imagen", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                        .addOnFailureListener {
//                            Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show()
//                        }
//
//                } else {
//                    Toast.makeText(this, "Error al crear usuario: ${task.exception?.message}", Toast.LENGTH_LONG).show()
//                }
//            }
//    }
//
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        val ivFoto = findViewById<ImageView>(R.id.ivFoto)
//
//        if (resultCode == RESULT_OK) {
//            when (requestCode) {
//                REQUEST_IMAGE_CAPTURE -> {
//                    val photo = data?.extras?.get("data") as? Bitmap
//                    photo?.let {
//                        ivFoto.setImageBitmap(it)
//                        // Guarda temporalmente en MediaStore y obtiene su Uri
//                        val path = MediaStore.Images.Media.insertImage(contentResolver, it, "FotoPerfil", null)
//                        imageUri = Uri.parse(path)
//                    }
//                }
//                REQUEST_IMAGE_PICK -> {
//                    imageUri = data?.data
//                    ivFoto.setImageURI(imageUri)
//                }
//            }
//        }
//    }
//
//
//    private fun limpiarForm() {
//        etNombre.text.clear()
//        etCorreo.text.clear()
//        etContraseña.text.clear()
//        rgGenero.clearCheck()
//        checkDeportes.isChecked = false
//        checkPolitica.isChecked = false
//        checkSalud.isChecked = false
//        checkCultura.isChecked = false
//        checkEducacion.isChecked = false
//        spnProvincias.setSelection(0)
//    }
//
//    private fun limpiardatos() {
//        limpiarForm()
//        cardResultado.visibility = View.GONE
//    }
//
//    private fun irHome() {
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//        finish()
//    }
//
//
//}




//    private fun enviarDatos() {
//        val nombre = etNombre.text.toString().trim()
//        val correo = etCorreo.text.toString().trim()
//        val contraseña = etContraseña.text.toString().trim()
//
//        // Validaciones nombre
//        if (nombre.isEmpty()) {
//            etNombre.error = "Debe ingresar datos válidos"
//            return
//        } else if (nombre.length < 8) {
//            etNombre.error = "El nombre debe tener al menos 8 caracteres"
//            return
//        } else if (nombre.any { it.isDigit() }) {
//            etNombre.error = "El nombre no debe contener números"
//            return
//        } else {
//            etNombre.error = null
//        }
////validaciones correo
//        if (correo.isEmpty()) {
//            etCorreo.error = "Debe ingresar datos válidos"
//            return
//        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches())  {
//            etCorreo.error = "Dominio de correo no valido"
//            return
//        } else {
//            etCorreo.error = null
//        }
////validaciones contraseña
//        if (contraseña.length < 8 || !contraseña.any { it.isDigit() } || !contraseña.any { it.isUpperCase() }) {
//            etContraseña.error = "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número"
//            return
//        } else {
//            etContraseña.error = null
//        }
//
//        val idGeneroSeleccionado = rgGenero.checkedRadioButtonId
//        if (idGeneroSeleccionado == -1) {
//            Toast.makeText(this, "Debe seleccionar un género", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val genero = findViewById<RadioButton>(idGeneroSeleccionado).text.toString()
//        val provincias = spnProvincias.selectedItem.toString()
//
//        val categoria = mutableListOf<String>().apply {
//            if (checkDeportes.isChecked) add("Deportes")
//            if (checkCultura.isChecked) add("Cultura")
//            if (checkEducacion.isChecked) add("Educación")
//            if (checkSalud.isChecked) add("Salud")
//            if (checkPolitica.isChecked) add("Política")
//        }.joinToString(", ")
//
//        val usuario = Usuario(
//            nombre = nombre,
//            correo = correo,
//            genero = genero,
//            categoria = categoria,
//            provincias = provincias
//        )
//
//
//    }