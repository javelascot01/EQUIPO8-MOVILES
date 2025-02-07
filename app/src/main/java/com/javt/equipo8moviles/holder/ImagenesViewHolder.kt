package com.javt.equipo8moviles.holder

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.javt.equipo8moviles.databinding.ImagenItemBinding

class ImagenesViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    private val binding = ImagenItemBinding.bind(itemView)
    val imagenView: ImageView = binding.imageView
}