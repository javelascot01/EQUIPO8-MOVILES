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

    private val _intentosRestantes = MutableLiveData(5)
    val intentosRestantes: LiveData<Int> get() = _intentosRestantes

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
        _intentosRestantes.value = juego.obtenerIntentosRestantes()
    }

    fun obtenerImagenesSegunDificultad(nivel: Int): List<Imagen> {
        return when (nivel) {
            0 -> imagenesFacil
            1 -> imagenesDificil
            else -> imagenesFacil
        }
    }

    fun procesarIntento(acierto: Boolean, tiempo: Long, imagen: Imagen) {
        juego.registrarIntento(acierto, tiempo, imagen)
        _puntuacion.value = juego.obtenerPuntuacion()
        _intentosRestantes.value = juego.obtenerIntentosRestantes()
        _imagenesAcertadas.value = juego.obtenerImagenesAcertadas()
    }
}
