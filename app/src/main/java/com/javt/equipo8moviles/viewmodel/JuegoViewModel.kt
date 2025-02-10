package com.javt.equipo8moviles.viewmodel

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.javt.equipo8moviles.model.Dificultad
import com.javt.equipo8moviles.model.Imagen
import com.javt.equipo8moviles.model.Juego
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polygon
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class JuegoViewModel : ViewModel() {
    private val juego = Juego()
    private var dificultad : Dificultad? = null;
    private val _puntuacion = MutableLiveData(0)
    val puntuacion: LiveData<Int> get() = _puntuacion

    private val _intentos = MutableLiveData(5) // Se ajustará según el nivel
    val intentos: LiveData<Int> get() = _intentos

    private val _imagenesAcertadas = MutableLiveData<List<Imagen>>(emptyList())
    val imagenesAcertadas: LiveData<List<Imagen>> get() = _imagenesAcertadas

    private val imagenesFacil = listOf(
        Imagen("paella", 39.4699, -0.3763, "Valencia","Paella Valenciana"),
        Imagen("tartasantiago", 42.8806, -8.5456, "Santiago de Compostela","Tartas de San Santiago"),
    )

    private val imagenesDificil = listOf(
        Imagen("cocidomadrileno", 40.4168, -3.7038, "Madrid", "Cocido Madrileño")
    )

    fun iniciarJuego(dificultad: Dificultad) {
        this.dificultad = dificultad
        juego.resetIntentos(dificultad)
        _intentos.value = juego.obtenerIntentosRestantes()
        _puntuacion.value = 0
        _imagenesAcertadas.value = emptyList()
    }

    fun obtenerImagenesSegunDificultad(dificultad: Dificultad?): List<Imagen> {
        return when (dificultad) {
            Dificultad.FACIL -> imagenesFacil
            Dificultad.DIFICIL -> imagenesDificil
            else -> emptyList()
        }
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
        Log.e("JuegoViewModel", "Intentando agregar imagen: ${imagen.nombre}")

        if (!juego.imagenYaAcertada(imagen)) {
            juego.registrarIntento(true, imagen)
            _imagenesAcertadas.postValue(juego.obtenerImagenesAcertadas().toList())

            Log.e("JuegoViewModel", "Imagen agregada a LiveData. Total: ${_imagenesAcertadas.value?.size}")
        } else {
            Log.e("JuegoViewModel", "La imagen ya estaba en la lista.")
        }
    }
    fun calcularDistancia(p1: GeoPoint, p2: GeoPoint): Double {
        // Formula de Haversine para calcular la distancia entre dos puntos
        val r = 6371000
        val dLat = Math.toRadians(p2.latitude - p1.latitude)
        val dLon = Math.toRadians(p2.longitude - p1.longitude)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(p1.latitude)) * cos(Math.toRadians(p2.latitude)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return r * c
    }
    fun createCircle(center: GeoPoint): Polygon {
        val radius= when(dificultad)
        {
            Dificultad.FACIL -> 100000.0
            Dificultad.DIFICIL -> 50000.0
            else -> 100000.0
        }
        return Polygon().apply {
            points = Polygon.pointsAsCircle(center, radius)
            fillColor = 0x40FF0000
            strokeColor = Color.RED
            strokeWidth = 3f
        }
    }
    fun isAcierto(distancia: Double): Boolean {
            return when (dificultad) {
                Dificultad.FACIL -> distancia <= 100000.0
                Dificultad.DIFICIL -> distancia <= 50000.0
                else -> false
            }
    }

    fun getDificultad(): Dificultad? {
        return dificultad
    }



}


