
package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.model.Publicacion
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnMenu: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var chipGroupFiltros: ChipGroup

    private var publicaciones: List<Publicacion> = emptyList()
    private lateinit var adapter: PublicacionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        chipGroupFiltros = findViewById(R.id.chipGroupFiltros)

        adapter = PublicacionAdapter(emptyList()) { publicacion ->
            val intent = Intent(this, DetallePublicacionActivity::class.java)
            intent.putExtra("publicacion", publicacion)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        PublicacionAdapter.cargarPublicaciones(
            onResult = { publicacionesCargadas ->
                publicaciones = publicacionesCargadas
                adapter.actualizarLista(publicaciones)
            },
            onError = { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )

        chipGroupFiltros.setOnCheckedChangeListener { _, checkedId ->
            val filtro = when (checkedId) {
                R.id.chipPolitica -> "Política"
                R.id.chipDeportes -> "Deportes"
                R.id.chipCultura -> "Cultura"
                R.id.chipEducacion -> "Educacion"
                R.id.chipSalud -> "Salud"
                else -> null
            }
            if (filtro != null) {
                // Log para depuración
                publicaciones.forEach {
                    Log.d("CATEGORIA", "Publicación: ${it.titulo}, Categoría: ${it.categoria}")
                }
                val filtradas = publicaciones.filter {
                    it.categoria.equals(filtro, ignoreCase = true)
                }
                adapter.actualizarLista(filtradas)
                if (filtradas.isEmpty()) {
                    Toast.makeText(this, "No hay publicaciones en $filtro", Toast.LENGTH_SHORT).show()
                }
            } else {
                adapter.actualizarLista(publicaciones)
            }
        }

        val fabAgregar = findViewById<FloatingActionButton>(R.id.fab_agregar)
        fabAgregar.setOnClickListener {
            val intent = Intent(this, CrearpubliActivity::class.java)
            startActivity(intent)
        }
        val btnEditarPublicaciones = findViewById<Button>(R.id.btnEditarPublicaciones)
        btnEditarPublicaciones.setOnClickListener {
            val intent = Intent(this, MisPublicacionesActivity::class.java)
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

    override fun onResume() {
        super.onResume()
        cargarPublicaciones()
    }

    private fun cargarPublicaciones() {
        PublicacionAdapter.cargarPublicaciones(
            onResult = { publicacionesCargadas ->
                publicaciones = publicacionesCargadas
                adapter.actualizarLista(publicaciones)
            },
            onError = { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}