package com.javt.equipo8moviles.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.javt.equipo8moviles.adapter.AdaptadorImagenes
import com.javt.equipo8moviles.databinding.ActivityMainBinding
import com.javt.equipo8moviles.databinding.ActivityPantallaImagenesBinding

class PantallaImagenes : AppCompatActivity() {
    private lateinit var binding: ActivityPantallaImagenesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantallaImagenesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuramos el RecyclerView
        initRecyclerView()
    }
    private fun initRecyclerView() {
        val manager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false)
        binding.rvImages.layoutManager = manager
        // Para el efecto de "snap"
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvImages)


//        binding.rvImages.adapter =
//            AdaptadorImagenes(ImagenesProvider.imagenesList)
    }
}