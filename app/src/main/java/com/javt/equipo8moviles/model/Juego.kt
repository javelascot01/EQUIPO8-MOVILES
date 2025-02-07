package com.javt.equipo8moviles.model

data class Lugar(val nombre: String, val latitud: Double, val longitud: Double)

class Juego {
    private var intentosRestantes: Int = 5
    private var puntuacion: Int = 0
    private val lugaresAcertados = mutableListOf<LugarAcertado>()

    fun resetIntentos(nivel: Int) {
        intentosRestantes = when (nivel) {
            1 -> 5  // Fácil
            2 -> 3  // Difícil
            else -> 5
        }
    }

    fun registrarIntento(acierto: Boolean, tiempo: Long, lugar: Lugar) {
        if (acierto) {
            val puntos = calcularPuntos()
            puntuacion += puntos
            lugaresAcertados.add(LugarAcertado(lugar, tiempo, puntos))
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
    fun obtenerLugaresAcertados(): List<LugarAcertado> = lugaresAcertados
}

data class LugarAcertado(val lugar: Lugar, val tiempo: Long, val puntos: Int)
