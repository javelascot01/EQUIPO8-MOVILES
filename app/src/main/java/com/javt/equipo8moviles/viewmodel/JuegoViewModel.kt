package com.javt.equipo8moviles.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.javt.equipo8moviles.model.Imagen
import com.javt.equipo8moviles.model.Juego


class JuegoViewModel : ViewModel() {
    private val juego = Juego()

    private val _puntuacion = MutableLiveData(0)
    val puntuacion: LiveData<Int> get() = _puntuacion

    private val _intentos = MutableLiveData(5) // Se ajustará según el nivel
    val intentos: LiveData<Int> get() = _intentos

    private val _imagenesAcertadas = MutableLiveData<List<Imagen>>(emptyList())
    val imagenesAcertadas: LiveData<List<Imagen>> get() = _imagenesAcertadas

    private val imagenesFacil = listOf(
        Imagen("paella", 39.4699, -0.3763, "Valencia"),
        Imagen("tartasantiago", 42.8806, -8.5456, "Santiago de Compostela")
    )

    private val imagenesDificil = listOf(
        Imagen("imagen6.jpg", -33.8688, 151.2093, "Sydney"),
        Imagen("imagen7.jpg", 55.7558, 37.6173, "Moscow"),
        Imagen("imagen8.jpg", 39.9042, 116.4074, "Beijing"),
        Imagen("imagen9.jpg", 19.4326, -99.1332, "Mexico City"),
        Imagen("imagen10.jpg", 37.7749, -122.4194, "San Francisco")
    )

    fun iniciarJuego(nivel: Int) {
        juego.resetIntentos(nivel)
        _intentos.value = juego.obtenerIntentosRestantes()
        _puntuacion.value = 0
        _imagenesAcertadas.value = emptyList()
    }

    fun obtenerImagenesSegunDificultad(nivel: Int): List<Imagen> {
        return if (nivel == 1) imagenesDificil else imagenesFacil
    }

    fun procesarIntento(acierto: Boolean, imagen: Imagen) {
        juego.registrarIntento(acierto, imagen)
        _intentos.value = juego.obtenerIntentosRestantes()
        _puntuacion.value = juego.obtenerPuntuacion()
        if (acierto) {
            _imagenesAcertadas.value = juego.obtenerImagenesAcertadas()
        }
    }

    fun juegoTerminado(): Boolean {
        return _intentos.value == 0
    }
    fun imagenYaAcertada(imagen: Imagen): Boolean {
        return juego.imagenYaAcertada(imagen)
    }
    fun agregarImagenAcertada(imagen: Imagen) {
        if (!juego.imagenYaAcertada(imagen)) {
            juego.registrarIntento(true, imagen)
            _imagenesAcertadas.value = juego.obtenerImagenesAcertadas()
        }
    }
}


