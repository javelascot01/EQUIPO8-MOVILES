package com.javt.equipo8moviles.view

import android.net.Uri
import android.widget.MediaController
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.javt.equipo8moviles.R
import com.javt.equipo8moviles.databinding.ActivityContenedorVideoBinding

class ActivityVideo : AppCompatActivity() {
    private lateinit var binding: ActivityContenedorVideoBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContenedorVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar controles del video
        val mediaControls = MediaController(this)
        mediaControls.setAnchorView(binding.vv)
        binding.vv.setMediaController(mediaControls)

        // Obtener el nombre del video y la imagen a mostrar
        val nombreVideo = intent.getStringExtra("nombreVideo")
        val nombreImagen = intent.getStringExtra("nombreImagen")
        val videoResId = resources.getIdentifier(nombreVideo, "raw", packageName)
        binding.vv.setZOrderOnTop(true) // Para que los controles del video se muestren por encima de otros elementos

        // Si se encontró el video, mostrarlo y configurar los botones
        if (videoResId != 0) {
            binding.txtVideos.text = nombreImagen
            binding.vv.setVideoURI(Uri.parse("android.resource://$packageName/$videoResId"))
            binding.vv.requestFocus() // Asegurar que se renderice correctamente

            binding.vv.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.setScreenOnWhilePlaying(true) // Mantener la pantalla encendida
            }
        } else {
            // Si no se encontró el video, mostrar un mensaje en su lugar y deshabilitar los botones
            binding.txtVideos.text = getString(R.string.pantalla_en_construccion)
            binding.botPlay.isEnabled = false
            binding.botPausar.isEnabled = false
            binding.botContinuar.isEnabled = false
            binding.botDetener.isEnabled = false
            Log.e("Video", "No se encontró el video con nombre: $nombreVideo")
        }

        binding.botPlay.setOnClickListener {
            binding.vv.start()
        }

        binding.botPausar.setOnClickListener {
            binding.vv.pause()
        }

        binding.botContinuar.setOnClickListener {
            binding.vv.start()
        }

        binding.botDetener.setOnClickListener {
            binding.vv.stopPlayback()
            binding.vv.setVideoURI(Uri.parse("android.resource://$packageName/$videoResId"))
        }
    }
}