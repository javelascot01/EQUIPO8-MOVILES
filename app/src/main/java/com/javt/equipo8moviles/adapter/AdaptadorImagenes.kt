package com.javt.equipo8moviles.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.javt.equipo8moviles.R
import com.javt.equipo8moviles.holder.ImagenesViewHolder

class AdaptadorImagenes(private val imageNames: List<String>) :
    RecyclerView.Adapter<ImagenesViewHolder>() {
    private var data: List<String>
    init {
        data = imageNames
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ImagenesViewHolder {
        // Inflamos el layout de cada elemento
        val layoutInflater = LayoutInflater.from(parent.context)
        return ImagenesViewHolder(layoutInflater.inflate(
            R.layout.imagen_item,
            parent, false))
    }
    override fun onBindViewHolder(holder: ImagenesViewHolder,
                                  position: Int) {
        // Inicializamos la lista de imagenes
        val imagenName = data[holder.adapterPosition]
        //accedo al imageView, por el nombre
        val imageResourceId =
            holder.itemView.context.resources.getIdentifier(
                imagenName,"drawable",holder.itemView.context.packageName)
        holder.imagenView.setImageResource(imageResourceId)
    }
    override fun getItemCount(): Int {
        return data.size
    }
}