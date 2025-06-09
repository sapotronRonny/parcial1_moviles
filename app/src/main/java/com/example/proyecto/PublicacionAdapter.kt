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
import com.google.firebase.Timestamp

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

            // Imagen en base64
            if (!publicacion.urlImagen.isNullOrEmpty()) {
                try {
                    val imageBytes = Base64.decode(publicacion.urlImagen, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    imagen.setImageBitmap(bitmap)
                    imagen.visibility = View.VISIBLE
                } catch (e: Exception) {
                    imagen.setImageResource(android.R.color.darker_gray)
                    imagen.visibility = View.GONE
                }
            } else {
                imagen.visibility = View.GONE
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
                    val publicaciones = mutableListOf<Publicacion>()
                    val docs = result.documents
                    if (docs.isEmpty()) {
                        onResult(emptyList())
                        return@addOnSuccessListener
                    }
                    var pendientes = docs.size
                    for (doc in docs) {
                        val idUsuario = doc.getString("idUsuario") ?: ""
                        db.collection("usuarios").document(idUsuario)
                            .get()
                            .addOnSuccessListener { userDoc ->
                                val autorNombre = userDoc.getString("nombre") ?: "Desconocido"
                                // Manejo correcto de la fecha tipo Timestamp
                                val creadoEnValue = doc.get("creadoEn")
                                val creadoEn = when (creadoEnValue) {
                                    is Timestamp -> creadoEnValue.toDate()
                                    is Long -> java.util.Date(creadoEnValue)
                                    is String -> creadoEnValue // Si la guardas como string
                                    else -> null
                                }
                                publicaciones.add(
                                    Publicacion(
                                        id = doc.id,
                                        idUsuario = idUsuario,
                                        autorNombre = autorNombre,
                                        titulo = doc.getString("titulo") ?: "",
                                        cuerpo = doc.getString("cuerpo") ?: "",
                                        urlImagen = doc.getString("urlImagen") ?: "",
                                        creadoEn = creadoEn
                                    )
                                )
                                pendientes--
                                if (pendientes == 0) {
                                    onResult(publicaciones)
                                }
                            }
                            .addOnFailureListener { e ->
                                pendientes--
                                if (pendientes == 0) {
                                    onResult(publicaciones)
                                }
                            }
                    }
                }
                .addOnFailureListener { e ->
                    onError(e)
                }
        }
    }
}