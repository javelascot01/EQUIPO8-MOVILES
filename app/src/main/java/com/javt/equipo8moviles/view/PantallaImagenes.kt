package com.javt.equipo8moviles.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.javt.equipo8moviles.R
import com.javt.equipo8moviles.adapter.AdaptadorImagenes
import com.javt.equipo8moviles.databinding.ActivityPantallaImagenesBinding
import com.javt.equipo8moviles.model.Dificultad
import com.javt.equipo8moviles.model.Imagen
import com.javt.equipo8moviles.view.fragments.MapFragment
import com.javt.equipo8moviles.viewmodel.JuegoViewModel

class PantallaImagenes : AppCompatActivity() {
    private lateinit var binding: ActivityPantallaImagenesBinding
    private val viewModel: JuegoViewModel by viewModels()
    private lateinit var difficulty: Dificultad
    private var imagenActual: Imagen? = null // Para rastrear la imagen seleccionada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantallaImagenesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener dificultad desde el Intent
        val intDificultad = intent.getIntExtra("difficulty", 0) // Si la obtengo como objeto peta
        difficulty = Dificultad.values()[intDificultad]
        viewModel.iniciarJuego(difficulty)

        // Configurar RecyclerView
        initRecyclerView()

        // Observar cambios en la puntuación e intentos restantes
        viewModel.puntuacion.observe(this) { puntuacion ->
            binding.textPuntuacion.text = "Puntuación: $puntuacion"
        }

        viewModel.intentos.observe(this) { intentos ->
            binding.textIntentos.text = "Intentos restantes: $intentos"
        }
    }

    private fun initRecyclerView() {
        val manager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvImages.layoutManager = manager
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvImages)

        binding.rvImages.adapter = AdaptadorImagenes(
            viewModel.obtenerImagenesSegunDificultad(difficulty),
            supportFragmentManager // Pasar el FragmentManager a AdaptadorImagenes para el fragmento del mapa
        ) { imagen -> onImageSelected(imagen) }
    }

    private fun onImageSelected(imagen: Imagen) {
        // Si la imagen ya fue seleccionada y acertada, no hacer nada
        viewModel.imagenesAcertadas.value?.let { lista ->
            if (lista.contains(imagen)) {
                Toast.makeText(this, getString(R.string.ya_acertaste_esta_imagen), Toast.LENGTH_SHORT).show()
                return
            }
        }
        imagenActual = imagen // Guardar la imagen actual

        // Mostrar fragmento del mapa con la nueva imagen
        val fragment = MapFragment.newInstance(imagen.nombre)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commitAllowingStateLoss()
    }

}