package com.example.proyecto

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun guardarSesionActiva(correo: String) {
        prefs.edit().putString("usuario_actual", correo).apply()
    }

    fun obtenerUsuarioActual(): String? {
        return prefs.getString("usuario_actual", null)
    }

    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }

    fun isUserLoggedIn(): Boolean {
        return prefs.contains("usuario_actual")
    }
}
