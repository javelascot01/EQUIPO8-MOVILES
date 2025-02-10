package com.javt.equipo8moviles.holder

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.javt.equipo8moviles.databinding.ItemPuntuacionBinding
import com.javt.equipo8moviles.model.Imagen

class PuntuacionViewHolder(private val binding: ItemPuntuacionBinding) : RecyclerView.ViewHolder(binding.root) {
    val imagenView: ImageView = binding.imageView
    fun bind(imagen: Imagen) {
        binding.tvLugar.text = imagen.lugar
        binding.tvNombre.text = imagen.nombre

    }
}