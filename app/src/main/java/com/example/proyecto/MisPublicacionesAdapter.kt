package com.example.proyecto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.model.Publicacion

class MisPublicacionesAdapter(
    private var publicaciones: List<Publicacion>,
    private val onEditar: (Publicacion) -> Unit,
    private val onEliminar: (Publicacion, () -> Unit) -> Unit
) : RecyclerView.Adapter<MisPublicacionesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tvTitulo)
        val tvCuerpo: TextView = view.findViewById(R.id.tvCuerpo)
        val btnEditar: Button = view.findViewById(R.id.btnEditar)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mi_publicacion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val publicacion = publicaciones[position]
        holder.tvTitulo.text = publicacion.titulo
        holder.tvCuerpo.text = publicacion.cuerpo
        holder.btnEditar.setOnClickListener { onEditar(publicacion) }
        holder.btnEliminar.setOnClickListener {
            onEliminar(publicacion) {
                publicaciones = publicaciones.filter { it.id != publicacion.id }
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount() = publicaciones.size
}