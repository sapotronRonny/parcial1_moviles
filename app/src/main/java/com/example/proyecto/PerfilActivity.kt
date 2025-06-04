package com.example.proyecto

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore



class PerfilActivity : AppCompatActivity() {

    private lateinit var listViewUser:ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)//inicializamos firebase
        setContentView(R.layout.activity_registros)

        listViewUser = findViewById(R.id.lvUsuarios)
        loadUser(
            onResult = {lista -> val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, lista)
                listViewUser.adapter = adapter
            },
            onError = {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        )

    }

    private fun loadUser(
        onResult:(List<String>) -> Unit,
        onError: (Exception) -> Unit
    ){
        val db = FirebaseFirestore.getInstance()
        db.collection("usuarios").get().addOnSuccessListener { result -> val lista = mutableListOf<String>()
            for (doc in result){
                val nombre = doc.getString("nombre")
                val correo = doc.getString("correo")
                val genero = doc.getString("genero")
                val noticia = doc.getString("Categoria de Noticias")
                val provincias = doc.getString("provincias")
                lista.add("$nombre - $correo\n$genero | $noticia | $provincias")
            }
            onResult(lista)

        }
            .addOnFailureListener{ onError(it)}
    }
}
