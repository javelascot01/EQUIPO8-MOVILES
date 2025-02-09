package com.javt.equipo8moviles.view

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.javt.equipo8moviles.adapter.PuntuacionAdapter
import com.javt.equipo8moviles.databinding.ActivityPuntuacionBinding
import com.javt.equipo8moviles.model.Imagen
import com.javt.equipo8moviles.viewmodel.JuegoViewModel

class PuntuacionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPuntuacionBinding
    private lateinit var imagenesAcertadas: ArrayList<Imagen>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuntuacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imagenesAcertadas = intent.getSerializableExtra("imagenesAcertadas") as? ArrayList<Imagen> ?: arrayListOf()

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        Log.e("PuntuacionActivity", "Adapter creado")

        val adapter = PuntuacionAdapter(imagenesAcertadas)
        recyclerView.adapter = adapter

    }
}