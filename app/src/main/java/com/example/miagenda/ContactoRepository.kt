package com.example.miagenda

object ContactoRepository {

    private val contactos = mutableListOf<Contacto>();

    fun agregar(contacto: Contacto){

        contactos.add(contacto);
    }

    fun obtenerTodos(): List<Contacto> {

        return contactos.toList();
    }

    fun obtenerFavoritos(): List<Contacto>{

        return contactos.filter{it.favorite}
    }

    fun ordenarAlfabeticamente(): List<Contacto>{

        return contactos.sortedBy { it.name }

    }
}