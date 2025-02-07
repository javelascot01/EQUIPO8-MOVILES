package com.javt.equipo8moviles.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.javt.equipo8moviles.model.Juego
import com.javt.equipo8moviles.model.Lugar
import com.javt.equipo8moviles.model.LugarAcertado

class JuegoViewModel : ViewModel() {
    private val juego = Juego()

    private val _puntuacion = MutableLiveData(0)
    val puntuacion: LiveData<Int> get() = _puntuacion

    private val _intentosRestantes = MutableLiveData(5)
    val intentosRestantes: LiveData<Int> get() = _intentosRestantes

    private val _lugaresAcertados = MutableLiveData<List<LugarAcertado>>(emptyList())
    val lugaresAcertados: LiveData<List<LugarAcertado>> get() = _lugaresAcertados

    fun iniciarJuego(nivel: Int) {
        juego.resetIntentos(nivel)
        _intentosRestantes.value = juego.obtenerIntentosRestantes()
    }

    fun procesarIntento(acierto: Boolean, tiempo: Long, lugar: Lugar) {
        juego.registrarIntento(acierto, tiempo, lugar)
        _puntuacion.value = juego.obtenerPuntuacion()
        _intentosRestantes.value = juego.obtenerIntentosRestantes()
        _lugaresAcertados.value = juego.obtenerLugaresAcertados()
    }
}
