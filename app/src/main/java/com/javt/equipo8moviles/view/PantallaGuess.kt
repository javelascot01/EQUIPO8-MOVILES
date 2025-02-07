package com.javt.equipo8moviles.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.javt.equipo8moviles.R
import com.javt.equipo8moviles.databinding.ActivityPantallaGuessBinding

class PantallaGuess : AppCompatActivity() {
    private lateinit var binding: ActivityPantallaGuessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantallaGuessBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}