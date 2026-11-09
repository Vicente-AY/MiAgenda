package com.example.miagenda

import android.content.Context

object RepositoryProvider {

    private var instancia: AgendaRepository? = null;

    fun get(context: Context): AgendaRepository {
        return instancia ?: crear(context.applicationContext).also {instancia = it}
    }

    private fun crear(context: Context): AgendaRepository{

        return SqliteAgendaRepository(context)

    }

}