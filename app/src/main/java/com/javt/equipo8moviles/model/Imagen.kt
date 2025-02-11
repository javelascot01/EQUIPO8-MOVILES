package com.javt.equipo8moviles.model

import java.io.Serializable

data class Imagen(
    var ruta: String,
    var latitud: Double,
    var longitud: Double,
    var lugar: String,
    var nombre: String
) : Serializable

