package com.javt.equipo8moviles.view

import android.R
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.javt.equipo8moviles.databinding.ActivityMainBinding
import com.javt.equipo8moviles.model.Dificultad

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Obtener la mejor puntuación guardada en SharedPreferences

        prefs = getSharedPreferences("JuegoPrefs", Context.MODE_PRIVATE)
        val mejorPuntuacion = prefs.getInt("mejor_puntuacion", 0)
        binding.txtMejorPuntuacion.text =
            getString(com.javt.equipo8moviles.R.string.mejor_puntuacion)+mejorPuntuacion
        // CONFIGURAR SPINNER
        val spinner=binding.spinner
        val dificultades = Dificultad.entries.map { it.aString(this) }
        val adapter = ArrayAdapter(
                this,
        R.layout.simple_spinner_item,
        dificultades // Lista de dificultades
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter


        // Funcionalidad boton jugar
        binding.btnJugar.setOnClickListener {
            val selectedDifficulty = spinner.selectedItemPosition
            val intent = Intent(this, PantallaImagenes::class.java)
            intent.putExtra("difficulty", selectedDifficulty)
            startActivity(intent)
        }

    }
    override fun onResume() {
        super.onResume()
        val mejorPuntuacion = prefs.getInt("mejor_puntuacion", 0)
        binding.txtMejorPuntuacion.text =
            getString(com.javt.equipo8moviles.R.string.mejor_puntuacion) + mejorPuntuacion
    }
}