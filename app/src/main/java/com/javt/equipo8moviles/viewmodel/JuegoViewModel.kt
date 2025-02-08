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

    private val _intentos = MutableLiveData(3) // Número máximo de intentos
    val intentos: LiveData<Int> get() = _intentos

    val _imagenesAcertadas = mutableListOf<Imagen>()
    val imagenesAcertadas: LiveData<List<Imagen>> get() = MutableLiveData(_imagenesAcertadas)

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
    }

    fun obtenerImagenesSegunDificultad(nivel: Int): List<Imagen> {
        return when (nivel) {
            0 -> imagenesFacil
            1 -> imagenesDificil
            else -> imagenesFacil
        }
    }

    fun procesarIntento(acierto: Boolean, timestamp: Long, imagen: Imagen) {
        if (_intentos.value!! > 0) {
            if (acierto) {
                _puntuacion.value = _puntuacion.value!! + 1
            } else {
                _intentos.value = (_intentos.value!! - 1).coerceAtLeast(0) // Evita valores negativos
            }
        }
    }
    fun juegoTerminado(): Boolean {
        return _intentos.value == 0
    }
    // Método para registrar una imagen acertada
    fun agregarImagenAcertada(imagen: Imagen) {
        if (!_imagenesAcertadas.contains(imagen)) {
            _imagenesAcertadas.add(imagen)
        }
    }

    // Verificar si una imagen ya ha sido acertada
    fun imagenYaAcertada(imagen: Imagen): Boolean {
        return _imagenesAcertadas.contains(imagen)
    }
}
