package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.model.Publicacion
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.example.proyecto.DetallePublicacionActivity
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnMenu: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PublicacionAdapter
    private val publicaciones = mutableListOf<Publicacion>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = PublicacionAdapter(publicaciones) { publicacion ->
            // Acción al hacer click en un ítem
            val intent = Intent(this, DetallePublicacionActivity::class.java)
            intent.putExtra("publicacion", publicacion)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        cargarPublicaciones() // Llama a tu función para cargar datos


//        agregar redireccion a creador de publicacion

        val fabAgregar = findViewById<FloatingActionButton>(R.id.fab_agregar)
        fabAgregar.setOnClickListener {
            val intent = Intent(this, CrearpubliActivity::class.java)
            startActivity(intent)
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        btnMenu = findViewById(R.id.btnMenu)

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    Toast.makeText(this, "Perfil seleccionado", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, PerfilActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Modos", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Ajustes::class.java)
                    startActivity(intent)
                }

                R.id.nav_logout -> {
                    Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show()
                    cerrarSesion()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }



    private fun cargarPublicaciones() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseFirestore.getInstance()
            .collection("publicaciones")
            .whereEqualTo("idUsuario", userId)
            .get()
            .addOnSuccessListener { result ->
                publicaciones.clear()
                for (document in result) {
                    val data = document.data
                    val creadoEn = (data["creadoEn"] as? com.google.firebase.Timestamp)?.toDate()?.time ?: 0L
                    val publicacion = Publicacion(
                        id = document.id,
                        idUsuario = data["idUsuario"] as? String ?: "",
                        titulo = data["titulo"] as? String ?: "",
                        cuerpo = data["cuerpo"] as? String ?: "",
                        urlImagen = data["urlImagen"] as? String ?: "",
                        creadoEn = creadoEn
                    )
                    publicaciones.add(publicacion)
                }
                adapter.notifyDataSetChanged()
            }
    }


    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, MainActivity::class.java)
        // Esto limpia el stack de actividades para que no pueda volver con "atrás"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}


//package com.example.proyecto
//
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//
//class HomeActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_home)
//        findViewById<Button>(R.id.btnIrRegistro).setOnClickListener{
//            startActivity(Intent(this,RegisterActivity::class.java))
//        }
//        findViewById<Button>(R.id.btnVerRegistro).setOnClickListener{
//            startActivity(Intent(this,RegistrosActivity::class.java))
//        }
//    }
//}
