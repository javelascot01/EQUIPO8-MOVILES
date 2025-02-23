package com.javt.equipo8moviles.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.javt.equipo8moviles.R
import com.javt.equipo8moviles.holder.ImagenesViewHolder
import com.javt.equipo8moviles.model.Imagen
import com.javt.equipo8moviles.view.fragments.MapFragment

class AdaptadorImagenes(private val imagenes: List<Imagen>,
                        private val fragmentManager: FragmentManager,
                        private val onImageClick: (Imagen) -> Unit
) : RecyclerView.Adapter<ImagenesViewHolder>() {
    private var data: List<Imagen>
    init {
        data = imagenes
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
        // Eliminar el fragment, no funciona al volver a un estado anterior
        /*
        val fragment = fragmentManager.findFragmentById(R.id.fragmentContainer)
        if (fragment != null) {
            Log.d("AdaptadorImagenes", "Eliminando fragment existente")
            fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }*/

        // Inicializamos la lista de imagenes
        val imagenName = data[holder.adapterPosition]
        //accedo al imageView, por el nombre
        val imageResourceId =
            holder.itemView.context.resources.getIdentifier(
                imagenName.ruta,"drawable",holder.itemView.context.packageName)


        if (imageResourceId != 0) {
            holder.imagenView.setImageResource(imageResourceId)
        } else {
            Log.e("AdaptadorImagenes", "Image resource not found for name: ${imagenName.ruta}")
        }
        holder.itemView.setOnClickListener {
            onImageClick(imagenName) // Llamamos a la función de callback con la imagen seleccionada
        }
    }
    fun obtenerImagen(position: Int): Imagen? {
        return if (position in data.indices) data[position] else null
    }


    override fun getItemCount(): Int {
        return data.size
    }
}