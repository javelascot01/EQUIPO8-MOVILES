package com.javt.equipo8moviles.view

import android.R
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
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

        // Obtener las preferencias de la aplicación
        prefs = getSharedPreferences("JuegoPrefs", Context.MODE_PRIVATE)

        // Mostrar la mejor puntuación
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
        // Funcionalidad boton Acerca de
        binding.btnAcercaDe.setOnClickListener {
            val intent = Intent(this, AcercaDe::class.java)
            startActivity(intent)
        }
        // Funcionalidad boton mute
        binding.btnMute.setOnClickListener {
            toggleMute()
        }

    }
    // Método para actualizar la mejor puntuación al volver a la actividad
    override fun onResume() {
        super.onResume()
        val mejorPuntuacion = prefs.getInt("mejor_puntuacion", 0)
        binding.txtMejorPuntuacion.text =
            getString(com.javt.equipo8moviles.R.string.mejor_puntuacion) + mejorPuntuacion
    }


    // Método para alternar entre mutear y desmutear
    private fun toggleMute() {
        // Obtener el estado actual de mute
        val isMuted = prefs.getBoolean("isMuted", false)
        // Cambiar el estado de mute
        val newMuteState = !isMuted

        // Cambiar el estado de mute en las preferencias
        val editor = prefs.edit()
        editor.putBoolean("isMuted", newMuteState)
        editor.apply()

        // Mostrar mensaje de mutear o desmutear
        if (newMuteState) {
            Toast.makeText(this, "Mute ON", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Mute OFF", Toast.LENGTH_SHORT).show()
        }
    }


}