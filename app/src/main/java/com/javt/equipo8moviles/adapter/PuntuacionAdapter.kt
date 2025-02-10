package com.javt.equipo8moviles.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.javt.equipo8moviles.databinding.ItemPuntuacionBinding
import com.javt.equipo8moviles.holder.PuntuacionViewHolder
import com.javt.equipo8moviles.model.Imagen

class PuntuacionAdapter(private val imagenesAcertadas: List<Imagen>) :
    RecyclerView.Adapter<PuntuacionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PuntuacionViewHolder {
        val binding = ItemPuntuacionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PuntuacionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PuntuacionViewHolder, position: Int) {
        holder.bind(imagenesAcertadas[holder.adapterPosition])
        val imagen = imagenesAcertadas[holder.adapterPosition]
        val imageResourceId =
            holder.itemView.context.resources.getIdentifier(
                imagen.ruta,"drawable",holder.itemView.context.packageName)
        if (imageResourceId != 0) {
            holder.imagenView.setImageResource(imageResourceId)
        } else {
            Log.e("AdaptadorImagenes", "Image resource not found for name: ${imagen.ruta}")
        }
    }

    override fun getItemCount(): Int = imagenesAcertadas.size

}