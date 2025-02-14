package com.javt.equipo8moviles.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
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
        //Ocultar el fragment
        binding.fragmentContainer.visibility= View.GONE

        // Obtener dificultad desde el Intent
        val intDificultad = intent.getIntExtra("difficulty", 0) // Si la obtengo como objeto peta
        difficulty = Dificultad.values()[intDificultad]
        viewModel.iniciarJuego(difficulty)

        // Configurar RecyclerView
        initRecyclerView()

        // Observar cambios en la puntuación e intentos restantes
        viewModel.puntuacion.observe(this) { puntuacion ->
            binding.textPuntuacion.text = getString(R.string.puntuacion)+" "+ puntuacion
        }

        viewModel.intentos.observe(this) { intentos ->
            binding.textIntentos.text = getString(R.string.intentosrestantes)+" "+ intentos
        }
    }

    // Inicializar el RecyclerView con las imágenes
    private fun initRecyclerView() {
        val manager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvImages.layoutManager = manager
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvImages)

        val adapter = AdaptadorImagenes(
            viewModel.obtenerImagenesSegunDificultad(difficulty),
            supportFragmentManager
        ) { imagen -> onImageSelected(imagen) }

        binding.rvImages.adapter = adapter
        // Ocultar el fragmento cuando se desplaza la pantalla
        binding.rvImages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING ||
                    newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    binding.fragmentContainer.visibility = View.GONE
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // Recuperar la imagen centrada cuando el scroll se detiene
                    val centerView = snapHelper.findSnapView(manager)
                    val position = centerView?.let { recyclerView.getChildAdapterPosition(it) } ?: return
                    val imagenCentrada = adapter.obtenerImagen(position)

                    if (imagenActual == imagenCentrada) {
                        binding.fragmentContainer.visibility = View.VISIBLE
                    }
                }
            }
        })
    }
    private fun onImageSelected(imagen: Imagen) {
        viewModel.imagenesAcertadas.value?.let { lista ->
            if (lista.contains(imagen)) {
                val intent = Intent(this, ActivityVideo::class.java)
                intent.putExtra("nombreVideo", imagen.ruta)
                intent.putExtra("nombreImagen", imagen.nombre)
                startActivity(intent)
                Toast.makeText(this, getString(R.string.ya_acertaste_esta_imagen), Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Si la imagen seleccionada es la misma que ya está mostrada, no hacer nada
        if (imagenActual == imagen) return

        imagenActual = imagen // Guardar la imagen actual

        // Hacer visible el contenedor del fragmento
        binding.fragmentContainer.visibility = View.VISIBLE

        // Reemplazar el fragmento con la nueva imagen
        val fragment = MapFragment.newInstance(imagen.nombre)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commitAllowingStateLoss()
    }


    /*
    private fun onImageSelected(imagen: Imagen) {

        // Si la imagen ya fue seleccionada y acertada, no hacer nada
        viewModel.imagenesAcertadas.value?.let { lista ->
            if (lista.contains(imagen)) {
                val intent= Intent(this,ActivityVideo::class.java)
                intent.putExtra("nombreVideo",imagen.ruta)
                intent.putExtra("nombreImagen",imagen.nombre)
                startActivity(intent)
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
    }*/

}