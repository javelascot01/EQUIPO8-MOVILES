package com.javt.equipo8moviles.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.javt.equipo8moviles.adapter.PuntuacionAdapter
import com.javt.equipo8moviles.databinding.ActivityPuntuacionBinding
import com.javt.equipo8moviles.model.Imagen

class PuntuacionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPuntuacionBinding
    private lateinit var imagenesAcertadas: ArrayList<Imagen>
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuntuacionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = getSharedPreferences("JuegoPrefs", Context.MODE_PRIVATE)
        val mejorPuntuacion = prefs.getInt("mejor_puntuacion", 0)

        imagenesAcertadas = intent.getSerializableExtra("imagenesAcertadas") as? ArrayList<Imagen> ?: arrayListOf()
        val puntuacion = intent.getIntExtra("puntuacion", 0)
        if (puntuacion > mejorPuntuacion) {
            with(prefs.edit()) {
                putInt("mejor_puntuacion", puntuacion)
                apply()
            }
        }
        binding.txtPuntuacion.text = puntuacion.toString()
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        Log.e("PuntuacionActivity", "Adapter creado")

        val adapter = PuntuacionAdapter(imagenesAcertadas)
        recyclerView.adapter = adapter

    }
}