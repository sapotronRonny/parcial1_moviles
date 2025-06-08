package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Ajustes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        findViewById<Button>(R.id.btnClaro).setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        findViewById<Button>(R.id.btnOscuro).setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        val btnModificar = findViewById<Button>(R.id.btnModificarPerfil)
        btnModificar.setOnClickListener {
            val intent = Intent(this, ActualizarPerfil::class.java)
            startActivity(intent)
        }
        val btnEliminarCuenta = findViewById<Button>(R.id.btnEliminarCuenta)
        btnEliminarCuenta.setOnClickListener {
            val intent = Intent(this, EliminarCuenta::class.java)
            startActivity(intent)
        }
    }
}