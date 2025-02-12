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
    private lateinit var audioManager: AudioManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Obtener la mejor puntuación guardada en SharedPreferences

        prefs = getSharedPreferences("JuegoPrefs", Context.MODE_PRIVATE)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

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
        binding.btnAcercaDe.setOnClickListener {
            val intent = Intent(this, AcercaDe::class.java)
            startActivity(intent)
        }
        binding.btnMute.setOnClickListener {
            toggleMute()
        }

    }
    override fun onResume() {
        super.onResume()
        val mejorPuntuacion = prefs.getInt("mejor_puntuacion", 0)
        binding.txtMejorPuntuacion.text =
            getString(com.javt.equipo8moviles.R.string.mejor_puntuacion) + mejorPuntuacion
    }


    // Método para alternar entre mutear y desmutear
    private fun toggleMute() {
        val isMuted = prefs.getBoolean("isMuted", false)
        val newMuteState = !isMuted

        val editor = prefs.edit()
        editor.putBoolean("isMuted", newMuteState)
        editor.apply()

        // Mutear o desmutear
        if (newMuteState) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
            Toast.makeText(this, "Mute ON", Toast.LENGTH_SHORT).show()
        } else {
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume / 2, 0)
            Toast.makeText(this, "Mute OFF", Toast.LENGTH_SHORT).show()
        }
        updateMuteButton()
    }

    // Método para actualizar el texto del botón según el estado de mute
    private fun updateMuteButton() {
        val isMuted = prefs.getBoolean("isMuted", false)
        
    }

}