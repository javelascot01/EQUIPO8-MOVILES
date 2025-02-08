package com.javt.equipo8moviles.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.javt.equipo8moviles.R
import com.javt.equipo8moviles.adapter.AdaptadorImagenes
import com.javt.equipo8moviles.databinding.ActivityMainBinding
import com.javt.equipo8moviles.databinding.ActivityPantallaImagenesBinding
import com.javt.equipo8moviles.model.Dificultad
import com.javt.equipo8moviles.model.Imagen
import com.javt.equipo8moviles.view.fragments.MapFragment
import com.javt.equipo8moviles.viewmodel.JuegoViewModel

class PantallaImagenes : AppCompatActivity() {
    private lateinit var binding: ActivityPantallaImagenesBinding
    private lateinit var viewModel: JuegoViewModel
    private lateinit var difficulty: Dificultad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantallaImagenesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Inicializamos el ViewModel
        val intDificultad = intent.getIntExtra("difficulty", 0)
        difficulty = Dificultad.values()[intDificultad]
        viewModel = JuegoViewModel()
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


        binding.rvImages.adapter = AdaptadorImagenes(viewModel.obtenerImagenesSegunDificultad(difficulty.ordinal)) { imagen ->
            onImageSelected(imagen)
        }
    }
    private fun onImageSelected(imagen: Imagen) {
        Log.d("ImageSelected", "Imagen seleccionada: ${imagen.nombre}")
        if (!isFinishing) {
            val fragment: Fragment = MapFragment.newInstance(imagen.nombre, "")
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commitAllowingStateLoss()
        }
    }
}