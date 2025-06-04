package com.example.proyecto

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        val usuario = FirebaseAuth.getInstance().currentUser
        if (usuario != null) {
            //Ya esta logeado, saltar al home directamente
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return //Evita que se cargue el resto del codigo
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.mi_color_personalizado)
        }
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // Enlace clickeable para registro
        val linkRegistro = findViewById<TextView>(R.id.linkRegistro)
        val texto = "No tienes cuenta, registrate aquí"
        val spannable = SpannableString(texto)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@MainActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: android.text.TextPaint) {
                ds.isUnderlineText = false
                ds.color = Color.parseColor("#D4AF37")
            }
        }

        spannable.setSpan(clickableSpan, 18, texto.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        linkRegistro.text = spannable
        linkRegistro.movementMethod = LinkMovementMethod.getInstance()

        // Obtener campos de entrada
        val correoInput = findViewById<TextInputEditText>(R.id.editTextCorreo)
        val contraseñaInput = findViewById<TextInputEditText>(R.id.editTextContraseña)
        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)

        // Acción al presionar el botón
        btnIniciarSesion.setOnClickListener {
            val correo = correoInput.text.toString().trim()
            val contraseña = contraseñaInput.text.toString()

            val correoRegex = Regex("^[\\w.-]+@(?:gmail|outlook|hotmail|yahoo)\\.com$")
            val contraseñaRegex = Regex("^(?=.*[A-Z])(?=.*\\d).+$")

            var esValido = true

            if (!correoRegex.matches(correo)) {
                correoInput.error = "Correo inválido o dominio no permitido"
                esValido = false
            }

            if (!contraseñaRegex.matches(contraseña)) {
                contraseñaInput.error = "Debe tener al menos 1 mayúscula y 1 número"
                esValido = false
            }

            if (esValido) {
                auth.signInWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Redirigir a HomeActivity
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish() // Evita que vuelva al login con el botón atrás
                        } else {
                            Toast.makeText(this, "Error al iniciar sesión: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }
}




//package com.example.proyecto
//
//import android.content.Intent
//import android.graphics.Color
//import android.os.Bundle
//import android.text.SpannableString
//import android.text.Spanned
//import android.text.method.LinkMovementMethod
//import android.text.style.ClickableSpan
//import android.view.View
//import android.widget.Button
//import android.widget.TextView
//import android.widget.Toast
//import com.google.android.material.textfield.TextInputEditText
//import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.FirebaseApp
//
//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        FirebaseApp.initializeApp(this)
//        setContentView(R.layout.activity_main)
//
//        // Enlace clickeable para registro
//        val linkRegistro = findViewById<TextView>(R.id.linkRegistro)
//        val texto = "No tienes cuenta, registrate aquí"
//        val spannable = SpannableString(texto)
//
//        val clickableSpan = object : ClickableSpan() {
//            override fun onClick(widget: View) {
//                val intent = Intent(this@MainActivity, RegisterActivity::class.java)
//                startActivity(intent)
//            }
//
//            override fun updateDrawState(ds: android.text.TextPaint) {
//                ds.isUnderlineText = false
//                ds.color = Color.parseColor("#D4AF37")
//            }
//        }
//
//        spannable.setSpan(clickableSpan, 18, texto.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        linkRegistro.text = spannable
//        linkRegistro.movementMethod = LinkMovementMethod.getInstance()
//
//        // Obtener campos de entrada
//        val correoInput = findViewById<TextInputEditText>(R.id.editTextCorreo)
//        val contraseñaInput = findViewById<TextInputEditText>(R.id.editTextContraseña)
//        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion) // Asegúrate de tener este botón en tu XML
//
//        // Acción al presionar el botón
//        btnIniciarSesion.setOnClickListener {
//            val correo = correoInput.text.toString().trim()
//            val contraseña = contraseñaInput.text.toString()
//
//            val correoRegex = Regex("^[\\w.-]+@(?:gmail|outlook|hotmail|yahoo)\\.com$")
//            val contraseñaRegex = Regex("^(?=.*[A-Z])(?=.*\\d).+$")
//
//            var esValido = true
//
//            if (!correoRegex.matches(correo)) {
//                correoInput.error = "Correo inválido o dominio no permitido"
//                esValido = false
//            }
//
//            if (!contraseñaRegex.matches(contraseña)) {
//                contraseñaInput.error = "Debe tener al menos 1 mayúscula y 1 número"
//                esValido = false
//            }
//
//            if (esValido) {
//                Toast.makeText(this, "Validación exitosa", Toast.LENGTH_SHORT).show()
//                // Aquí puedes continuar con el login o cambiar de pantalla
//            }
//        }
//    }
//}