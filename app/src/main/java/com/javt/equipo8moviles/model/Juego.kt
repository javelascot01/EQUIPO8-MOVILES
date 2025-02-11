package com.javt.equipo8moviles.model

import android.util.Log

class Juego {
    private var intentosRestantes: Int = 5
    private var maxIntentos: Int = 5
    private var puntuacion: Int = 0
    private val imagenesAcertadas = mutableListOf<Imagen>()

    fun resetIntentos(dificultad: Dificultad) {
        maxIntentos = when (dificultad) {
            Dificultad.FACIL -> 5
            Dificultad.DIFICIL -> 3
            else -> 5
        }
        intentosRestantes = maxIntentos
    }

    fun registrarIntento(acierto: Boolean, imagen: Imagen) {
        if (acierto) {
            puntuacion += calcularPuntos()
            if (!imagenesAcertadas.contains(imagen)) {
                imagenesAcertadas.add(imagen)
                Log.e("Juego", "Imagen añadida: ${imagen.nombre}, total: ${imagenesAcertadas.size}")
            } else {
                Log.e("Juego", "La imagen ya estaba acertada: ${imagen.nombre}")
            }
        } else {
            intentosRestantes--
        }
    }

    private fun calcularPuntos(): Int {
        val intentosUsados = maxIntentos - intentosRestantes
        return 100 - intentosUsados * 20
    }

    fun obtenerPuntuacion(): Int = puntuacion
    fun obtenerIntentosRestantes(): Int = intentosRestantes
    fun obtenerImagenesAcertadas(): List<Imagen> = imagenesAcertadas.toList()
    fun imagenYaAcertada(imagen: Imagen): Boolean = imagenesAcertadas.contains(imagen)
}