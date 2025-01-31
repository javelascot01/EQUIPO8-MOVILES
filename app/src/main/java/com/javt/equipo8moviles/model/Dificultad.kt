package com.javt.equipo8moviles.model

import android.content.Context
import com.javt.equipo8moviles.R

enum class Dificultad(private val nivel: Int) {
    FACIL(R.string.dif_facil),
    MEDIA(R.string.dif_moderada),
    DIFICIL(R.string.dif_dificil);

    fun aString(context: Context): String {
        return context.getString(nivel)
    }

}