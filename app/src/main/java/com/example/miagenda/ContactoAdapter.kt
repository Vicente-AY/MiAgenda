package com.example.miagenda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.NonDisposableHandle.parent

class ContactoAdapter(
    private var contactos: List<Contacto>,
    private val onContactoClick: (Contacto) -> Unit) : RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder>(){

    class ContactoViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val nombre: TextView = view.findViewById(R.id.itemName);
        val telefono: TextView = view.findViewById(R.id.itemPhone);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoViewHolder{

        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_contacto, parent, false);
        return ContactoViewHolder(vista);
    }

    override fun onBindViewHolder(holder: ContactoViewHolder, position: Int){

        val contacto = contactos[position];
        holder.nombre.text = contacto.name;
        holder.telefono.text = contacto.phone;
        holder.itemView.setOnClickListener {onContactoClick(contacto)}
    }

    override fun getItemCount(): Int {

        return contactos.size;
    }

    fun actualizar(nuevos: List<Contacto>){

        contactos = nuevos;
        notifyDataSetChanged();
    }

}