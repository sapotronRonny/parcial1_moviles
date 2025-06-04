package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splasha)



        Handler(Looper.getMainLooper()).postDelayed({
            val sessionManager = SessionManager(this)

            val destination = if (sessionManager.isUserLoggedIn()) {
                Intent(this, HomeActivity::class.java)
            } else {
                Intent(this, MainActivity::class.java)
            }

            startActivity(destination)
            finish()
        }, 2000) // espera 2 segundos
    }
}
