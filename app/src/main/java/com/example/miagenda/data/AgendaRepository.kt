package com.example.miagenda.data

import com.example.miagenda.model.Contacto

interface AgendaRepository {

    fun obtenerTodos(onOk: (List<Contacto>) -> Unit, onError: (String) -> Unit);

    fun buscar(text: String, onOk: (List<Contacto>) -> Unit, onError: (String) -> Unit);

    fun obtenerPorId(id: String, onOk: (Contacto?) -> Unit, onError: (String) -> Unit);

    fun guardar(contacto: Contacto, onOk: () -> Unit, onError: (String) -> Unit);

    fun eliminar(id: String, onOk: () -> Unit, onError: (String) -> Unit);

    fun obtenerNumeroDeContactosPorCategoria(categoria: String, onOk: (Int) -> Unit, onError: (String) -> Unit);
}