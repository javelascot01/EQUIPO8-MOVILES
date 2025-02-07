package com.javt.equipo8moviles.model

class Juego {
    private var intentosRestantes: Int = 5
    private var puntuacion: Int = 0
    private val imagenesAcertadas = mutableListOf<Imagen>()

    fun resetIntentos(nivel: Int) {
        intentosRestantes = when (nivel) {
            1 -> 5  // Fácil
            2 -> 3  // Difícil
            else -> 5
        }
    }

    fun registrarIntento(acierto: Boolean, tiempo: Long, imagen: Imagen) {
        if (acierto) {
            val puntos = calcularPuntos()
            puntuacion += puntos
            imagenesAcertadas.add(imagen)
            resetIntentos(1)  // Reiniciar intentos para la siguiente imagen
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
}
