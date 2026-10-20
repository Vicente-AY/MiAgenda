package com.example.miagenda

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.net.toUri
import java.lang.System.out
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ContactoAdapter

    private val launcherForumlario = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        resultado ->
            if(resultado.resultCode == RESULT_OK){
                val datos = resultado.data;

                if(datos != null){
                    val nombre = datos.getStringExtra("nombre") ?: "";
                    val telefono = datos.getStringExtra("telefono") ?: "";
                    val email = datos.getStringExtra("email");
                    val favorito = datos.getBooleanExtra("favorito", false);

                    ContactoRepository.agregar(Contacto(nombre, telefono, email, favorito));

                    getSharedPreferences("miagendaprefs", MODE_PRIVATE).edit {
                        putString(
                            "ultimoContacto",
                            nombre
                        )
                    };

                    mostrarUltimoContacto()

                    refrescarLista()
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recycler = findViewById<RecyclerView>(R.id.recyclerContacts);
        recycler.layoutManager = LinearLayoutManager(this);
        adapter = ContactoAdapter(ContactoRepository.obtenerTodos()){ contacto ->
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = "tel:${contacto.phone}".toUri()

            startActivity(intent);
        }

        recycler.adapter = adapter;

        val btnAñadir = findViewById<Button>(R.id.addContact);

        btnAñadir.setOnClickListener {

            val intent = Intent(this, ContactoFormActivity::class.java);
            launcherForumlario.launch(intent);
        }

        actualizarContador();

        val soloFavoritos = findViewById<ToggleButton>(R.id.only_favourites);

        soloFavoritos.setOnCheckedChangeListener { _, _ ->
            refrescarLista();
        }

        mostrarUltimoContacto()
    }

    private fun actualizarContador(){

        findViewById<TextView>(R.id.contacts_count).text = ContactoRepository.obtenerTodos().size.toString();
    }

    private fun refrescarLista(){

        val soloFavoritos = findViewById<ToggleButton>(R.id.only_favourites);

        val lista: List<Contacto>

        if(soloFavoritos.isChecked){
            lista = ContactoRepository.obtenerFavoritos()
        }
        else{
            lista = ContactoRepository.obtenerTodos()
        }

        adapter.actualizar(lista);

    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState);

        val interruptor = findViewById<ToggleButton>(R.id.only_favourites);
        outState.putBoolean("soloFavoritos", interruptor.isChecked());
    }

    private fun mostrarUltimoContacto(){

        val prefs = getSharedPreferences("miagendaprefs", MODE_PRIVATE);

        val nombre = prefs.getString("ultimoContacto", null);

        findViewById<TextView>(R.id.lastContact).text = nombre ?: getString(R.string.no_last_contact);

    }
}