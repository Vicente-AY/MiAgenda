package com.example.miagenda

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.net.toUri
import java.lang.System.out
import androidx.core.content.edit
import com.example.miagenda.RepositoryProvider

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ContactoAdapter

    private val launcherForumlario = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        resultado ->
            if(resultado.resultCode == RESULT_OK){
                val datos = resultado.data;

                if(datos != null){
                    val id = datos.getStringExtra("id") ?: "";
                    val nombre = datos.getStringExtra("nombre") ?: "";
                    val telefono = datos.getStringExtra("telefono") ?: "";
                    val email = datos.getStringExtra("email");
                    val favorito = datos.getBooleanExtra("favorito", false);
                    val categoria = datos.getStringExtra("category") ?: "";
                    val notas = datos.getStringExtra("notes");

                    val contacto = Contacto(id, nombre, telefono, email, favorito, categoria, notas);

                    RepositoryProvider.get(this).guardar(contacto, {

                        getSharedPreferences("miagendaprefs", MODE_PRIVATE).edit {
                            putString("ultimoContacto", nombre)
                        }

                        mostrarUltimoContacto();
                        refrescarLista();
                        actualizarContador();
                    },
                    { error ->
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                    });
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

        adapter = crearAdapter();

        refrescarLista();

        recycler.adapter = adapter;

        val btnOpciones = findViewById<Button>(R.id.options);
        btnOpciones.setOnClickListener {

            intent = Intent(this, AjustesActivity::class.java);
            startActivity(intent);

        }

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

        val btnOrdenar = findViewById<ToggleButton>(R.id.sort);

        btnOrdenar.setOnClickListener {

            refrescarLista()

        }

        val busqueda = findViewById<EditText>(R.id.search_input);
        val btnBuscar = findViewById<Button>(R.id.search_button);
        btnBuscar.setOnClickListener {

            val texto = busqueda.text.toString().trim();

            if (!texto.isEmpty()) {

                RepositoryProvider.get(this).buscar(
                    texto, { lista ->

                        adapter.actualizar(lista);

                    },
                    { error ->
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                    });
            } else {

                RepositoryProvider.get(this).obtenerTodos(
                    onOk = { lista ->

                        adapter.actualizar(lista);
                    },
                    onError = { error ->
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show();


                    });


            }
        }

        mostrarUltimoContacto()
    }

    private fun crearAdapter(): ContactoAdapter{
        return ContactoAdapter(
            emptyList(),
            { contacto ->
                Log.d("MiAgenda", "Iniciando Llamada")
                realizarLlamada(contacto);

            },
            {contacto ->
                Log.d("MiAgenda", "MainActivity: Mostrar Menú Contextual")
                mostarMenuContextual(contacto);
            });
    }

    private fun realizarLlamada(contacto: Contacto){

        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = "tel:${contacto.phone}".toUri()

        startActivity(intent);
    }

    private fun actualizarContador(){

        RepositoryProvider.get(this).obtenerTodos(onOk = { lista ->
            val total = lista.size
            findViewById<TextView>(R.id.contacts_count).text = total.toString()
        },
            {mensaje ->
                Log.e("MiAgenda", "Error al actualizar contador: $mensaje")

            });

    }

    private fun refrescarLista(){

        val ordenar = findViewById<ToggleButton>(R.id.sort)
        val soloFavoritos = findViewById<ToggleButton>(R.id.only_favourites);

        RepositoryProvider.get(this).obtenerTodos(
            onOk = { listaCompleta ->
                var lista = listaCompleta

                if (soloFavoritos.isChecked) {
                    lista = lista.filter { it.favorite }
                }

                if (ordenar.isChecked) {
                    lista = lista.sortedBy { it.name.lowercase() }
                }

                adapter.actualizar(lista)
            },
            onError = { error ->
                Log.e("MiAgenda", "Error al refrescar lista: $error")
            }
        );

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

    private fun mostarMenuContextual(contacto: Contacto) {
        Log.d("MiAgenda", "MainActivity: Mostrando Menú Contextual")
        var items = resources.getStringArray(R.array.menu_items)

        AlertDialog.Builder(this).setItems(items) { _, which ->
            when (which) {
                0 -> vistaDetalles(contacto);
                1 -> modificarContacto(contacto);
                2 -> confirmarEliminarContacto(contacto);
            }
        }
        .show();
    }

    private fun confirmarEliminarContacto(contacto: Contacto){

        AlertDialog.Builder(this)
            .setMessage(getString(R.string.delete_message_alert) + " " + contacto.name + "?")
            .setPositiveButton(R.string.btn_delete) { _, _ ->
                RepositoryProvider.get(this).eliminar(
                    contacto.id,
                    onOk = {
                        refrescarLista()
                        actualizarContador()
                    },
                    onError = { error ->
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    }
                )
            }
            .setNegativeButton(R.string.cancel, null)
            .show();

    }

    private fun modificarContacto(contacto: Contacto) {

        val intent = Intent(this, ContactoFormActivity::class.java)
        intent.putExtra("id", contacto.id)
        intent.putExtra("nombre", contacto.name)
        intent.putExtra("telefono", contacto.phone)
        intent.putExtra("email", contacto.email)
        intent.putExtra("favorito", contacto.favorite)
        intent.putExtra("categoria", contacto.category)
        intent.putExtra("notas", contacto.notes)

        launcherForumlario.launch(intent)

    }

    private fun vistaDetalles(contacto: Contacto){

        val intent = Intent(this, ContactoDetalleActivity::class.java)
        intent.putExtra("id", contacto.id)
        intent.putExtra("nombre", contacto.name)
        intent.putExtra("telefono", contacto.phone)
        intent.putExtra("email", contacto.email)
        intent.putExtra("favorito", contacto.favorite)
        intent.putExtra("categoria", contacto.category)
        intent.putExtra("notas", contacto.notes)

        startActivity(intent);


    }



    override fun onStart() { super.onStart(); Log.d("MiAgenda", "MainActivity: onStart") }
    override fun onResume() {
        super.onResume(); Log.d("MiAgenda", "MainActivity: onResume")
        refrescarLista();
    }
    override fun onPause() { super.onPause(); Log.d("MiAgenda", "MainActivity: onPause") }
    override fun onStop() { super.onStop(); Log.d("MiAgenda", "MainActivity: onStop") }
    override fun onDestroy() { super.onDestroy(); Log.d("MiAgenda", "MainActivity: onDestroy") }
}