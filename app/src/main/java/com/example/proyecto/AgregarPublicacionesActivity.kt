package com.example.proyecto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AgregarPublicacionesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_publicaciones )

        supportActionBar?.title = "Nueva Publicación"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
