package com.example.miagenda

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ContactoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ContactoRepository.agregar(Contacto("Anita", "600111222", null, true));
        ContactoRepository.agregar(Contacto("Anota", "600111223", "luis@mail.com", false));

        Log.d("MiAgenda", "Total: ${ContactoRepository.obtenerTodos().size}");
        Log.d("MiAgenda", "Favortos: ${ContactoRepository.obtenerFavoritos().size}");

        val recycler = findViewById<RecyclerView>(R.id.recyclerContacts);
        recycler.layoutManager = LinearLayoutManager(this);
        adapter = ContactoAdapter(ContactoRepository.obtenerTodos()){ contacto ->
            Log.d("MiAgenda", "Puldado: ${contacto.name} con telefono ${contacto.phone}")
        }

        recycler.adapter = adapter;


        val btnAñadir = findViewById<Button>(R.id.addContact);

        btnAñadir.setOnClickListener {
            val intent = Intent(this, ContactoFormActivity::class.java);
            startActivity(intent);
        }
    }
}