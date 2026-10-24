package com.example.miagenda

import java.util.Locale
import java.util.Locale.getDefault

object ContactoRepository {

    private val contactos = mutableListOf<Contacto>();

    fun agregar(contacto: Contacto){

        contactos.add(contacto);
    }

    fun obtenerTodos(ordenar: Boolean): List<Contacto> {

        if(ordenar){
            return contactos.sortedBy { it.name.lowercase() }
        }
        else{
            return contactos.toList()
        }

    }

    fun obtenerFavoritos(ordenar: Boolean): List<Contacto>{

        if(ordenar){
            return contactos.filter{it.favorite}.sortedBy { it.name.lowercase() }
        }
        else{
            return contactos.filter{it.favorite}
        }
    }

    fun eliminar(contacto: Contacto){
        contactos.remove(contacto);
    }

    fun indiceDe(contacto: Contacto): Int{
        return contactos.indexOf(contacto);
    }

    fun actualizar(contacto: Contacto, indice: Int){
        contactos[indice] = contacto;
    }
}
