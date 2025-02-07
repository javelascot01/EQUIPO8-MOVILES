package com.javt.equipo8moviles.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.javt.equipo8moviles.adapter.PuntuacionAdapter
import com.javt.equipo8moviles.databinding.ActivityPuntuacionBinding
import com.javt.equipo8moviles.viewmodel.JuegoViewModel

class PuntuacionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPuntuacionBinding
    private val juegoViewModel: JuegoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuntuacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        juegoViewModel.imagenesAcertadas.observe(this, Observer { imagenes ->
            val adapter = PuntuacionAdapter(imagenes)
            recyclerView.adapter = adapter
        })
    }
}