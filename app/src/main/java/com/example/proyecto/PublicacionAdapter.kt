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
    private var publicaciones: List<Publicacion>,
    private val onItemClick: (Publicacion) -> Unit
) : RecyclerView.Adapter<PublicacionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.tvTitulo)
        val cuerpo: TextView = itemView.findViewById(R.id.tvCuerpo)
        val imagen: ImageView = itemView.findViewById(R.id.ivImagen)
        val autorNombre: TextView = itemView.findViewById(R.id.tvAutorNombre)

        fun bind(publicacion: Publicacion) {
            titulo.text = publicacion.titulo
            cuerpo.text = publicacion.cuerpo
            autorNombre.text = publicacion.autorNombre
            cuerpo.maxLines = 1
            cuerpo.ellipsize = android.text.TextUtils.TruncateAt.END
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

    // Método para actualizar la lista de publicaciones
    fun actualizarLista(nuevaLista: List<Publicacion>) {
        publicaciones = nuevaLista
        notifyDataSetChanged()
    }

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
                        val creadoEnValue = doc.get("creadoEn")
                        val creadoEn = if (creadoEnValue is Number) creadoEnValue.toLong() else 0L

                        Publicacion(
                            id = doc.id,
                            idUsuario = doc.getString("idUsuario") ?: "",
                            autorNombre = doc.getString("autorNombre") ?: "",
                            titulo = doc.getString("titulo") ?: "",
                            cuerpo = doc.getString("cuerpo") ?: "",
                            categoria = doc.getString("categoria") ?: "",
                            urlImagen = doc.getString("urlImagen") ?: "",
                            creadoEn = creadoEn
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