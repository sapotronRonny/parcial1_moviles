package com.example.proyecto

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.model.Publicacion
import com.google.firebase.firestore.FirebaseFirestore

class PublicacionAdapter(
    private val publicaciones: List<Publicacion>,
    private val onItemClick: (Publicacion) -> Unit
) : RecyclerView.Adapter<PublicacionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.tvTitulo)
        val cuerpo: TextView = itemView.findViewById(R.id.tvCuerpo)
        val imagen: ImageView = itemView.findViewById(R.id.ivImagen)
        fun bind(publicacion: Publicacion) {
            titulo.text = publicacion.titulo
            cuerpo.text = publicacion.cuerpo
            if (publicacion.urlImagen.isNotEmpty()) {
                try {
                    val imageBytes = Base64.decode(publicacion.urlImagen, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    imagen.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    imagen.setImageResource(android.R.color.darker_gray)
                }
            } else {
                imagen.setImageResource(android.R.color.darker_gray)
            }
            itemView.setOnClickListener { onItemClick(publicacion) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_publicacion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(publicaciones[position])
    }

    override fun getItemCount() = publicaciones.size

    companion object {
        fun cargarPublicaciones(
            onResult: (List<Publicacion>) -> Unit,
            onError: (Exception) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()
            db.collection("publicaciones")
                .get()
                .addOnSuccessListener { result ->
                    val publicaciones = result.map { doc ->
                        Publicacion(
                            titulo = doc.getString("titulo") ?: "",
                            cuerpo = doc.getString("cuerpo") ?: "",
                            urlImagen = doc.getString("urlImagen") ?: ""
                        )
                    }
                    onResult(publicaciones)
                }
                .addOnFailureListener { e ->
                    onError(e)
                }
        }
    }
}