package com.javt.equipo8moviles.model

import java.io.Serializable

data class Imagen(
    var nombre: String,
    var latitud: Double,
    var longitud: Double,
    var lugar: String
) : Serializable

