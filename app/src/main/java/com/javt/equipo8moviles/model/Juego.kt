package com.javt.equipo8moviles.model

class Juego {
    private var intentosRestantes: Int = 5
    private var puntuacion: Int = 0
    private val imagenesAcertadas = mutableListOf<Imagen>()

    fun resetIntentos(nivel: Int) {
        intentosRestantes = when (nivel) {
            0 -> 5  // Fácil
            1 -> 3  // Difícil
            else -> 5
        }
    }

    fun registrarIntento(acierto: Boolean, imagen: Imagen) {
        if (acierto) {
            puntuacion += calcularPuntos()
            if (!imagenesAcertadas.contains(imagen)) {
                imagenesAcertadas.add(imagen)
            }
        } else {
            intentosRestantes--
        }
    }

    private fun calcularPuntos(): Int {
        return when (intentosRestantes) {
            5 -> 100
            4 -> 80
            3 -> 60
            2 -> 40
            1 -> 20
            else -> 0
        }
    }

    fun obtenerPuntuacion(): Int = puntuacion
    fun obtenerIntentosRestantes(): Int = intentosRestantes
    fun obtenerImagenesAcertadas(): List<Imagen> = imagenesAcertadas
    fun imagenYaAcertada(imagen: Imagen): Boolean = imagenesAcertadas.contains(imagen)
}