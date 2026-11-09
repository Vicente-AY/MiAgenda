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
import androidx.appcompat.app.AlertDialog
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
                    val id = datos.getStringExtra("id") ?: "";
                    val nombre = datos.getStringExtra("nombre") ?: "";
                    val telefono = datos.getStringExtra("telefono") ?: "";
                    val email = datos.getStringExtra("email");
                    val favorito = datos.getBooleanExtra("favourite", false);
                    val categoria = datos.getStringExtra("category") ?: "";
                    val notas = datos.getStringExtra("notes");

                    val contacto = Contacto(id, nombre, telefono, email, favorito, categoria, notas);

                    if(id == ""){
                        ContactoRepository.agregar(contacto);
                        getSharedPreferences("miagenda_prefs", MODE_PRIVATE)
                            .edit()
                            .putString("ultimoContacto", nombre)
                            .apply();
                        mostrarUltimoContacto()
                    }
                    else{
                        ContactoRepository.actualizar(contacto, id);

                    }

                    getSharedPreferences("miagendaprefs", MODE_PRIVATE).edit {
                        putString(
                            "ultimoContacto",
                            nombre
                        )
                    };

                    mostrarUltimoContacto()

                    refrescarLista()
                    actualizarContador()
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

        mostrarUltimoContacto()

        //createPlaceholderData();
    }

    private fun crearAdapter(): ContactoAdapter{
        return ContactoAdapter(ContactoRepository.obtenerTodos(false),
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

        findViewById<TextView>(R.id.contacts_count).text = ContactoRepository.obtenerTodos(false).size.toString();
    }

    private fun refrescarLista(){

        val ordenar = findViewById<ToggleButton>(R.id.sort)
        val soloFavoritos = findViewById<ToggleButton>(R.id.only_favourites);

        val lista: List<Contacto>

        if(soloFavoritos.isChecked){
            lista = ContactoRepository.obtenerFavoritos(ordenar.isChecked)
        }
        else{
            lista = ContactoRepository.obtenerTodos(ordenar.isChecked)
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

    private fun mostarMenuContextual(contacto: Contacto) {
        Log.d("MiAgenda", "MainActivity: Mostrando Menú Contextual")
        var items = resources.getStringArray(R.array.menu_items)

        AlertDialog.Builder(this).setItems(items) { _, which ->
            when (which) {
                0 -> enviarEmail(contacto);
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
                ContactoRepository.eliminar(contacto)
                refrescarLista()
                actualizarContador()
            }
            .setNegativeButton(R.string.cancel, null)
            .show();

    }

    private fun modificarContacto(contacto: Contacto) {

        val intent = Intent(this, ContactoFormActivity::class.java)
        intent.putExtra("indice", ContactoRepository.indiceDe(contacto))
        intent.putExtra("nombre", contacto.name)
        intent.putExtra("telefono", contacto.phone)
        intent.putExtra("email", contacto.email)
        intent.putExtra("favorito", contacto.favorite)

        launcherForumlario.launch(intent)

    }

    private fun enviarEmail(contacto: Contacto){

        if(contacto.email == "" || contacto.email == null){

            AlertDialog.Builder(this)
                .setMessage(R.string.no_email_alert)
                .setPositiveButton(R.string.accept) { dialog, _ ->
                    dialog.dismiss();
                }
                .show();
        }
        else{
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = "mailto:${contacto.email}".toUri()

            startActivity(intent);
        }

    }

    private fun createPlaceholderData(){

        ContactoRepository.agregar(Contacto(
            id = "1",
            name = "Carlos García",
            phone = "612345678",
            email = "carlos@gmail.com",
            favorite = true,
            category = "Amigos",
            notes = "Amigo de la infancia"
        ))

        ContactoRepository.agregar(Contacto(
            id = "2",
            name = "María López",
            phone = "623456789",
            email = "maria@gmail.com",
            favorite = true,
            category = "Familia",
            notes = "Prima"
        ))

        ContactoRepository.agregar(Contacto(
            id = "3",
            name = "Juan Martínez",
            phone = "634567890",
            email = "juan@gmail.com",
            category = "Trabajo",
            notes = "Compañero de oficina"
        ))

        ContactoRepository.agregar(Contacto(
            id = "4",
            name = "Ana Sánchez",
            phone = "645678901",
            email = "ana@gmail.com",
            favorite = true,
            category = "Amigos",
            notes = "Amiga de la universidad"
        ))

        ContactoRepository.agregar(Contacto(
            id = "5",
            name = "Pedro Fernández",
            phone = "656789012",
            email = null,
            category = "Trabajo",
            notes = "Cliente habitual"
        ))

        ContactoRepository.agregar(Contacto(
            id = "6",
            name = "Laura González",
            phone = "667890123",
            email = "laura@gmail.com",
            favorite = true,
            category = "Familia",
            notes = "Hermana"
        ))

        ContactoRepository.agregar(Contacto(
            id = "7",
            name = "David Rodríguez",
            phone = "678901234",
            email = "david@gmail.com",
            category = "Amigos",
            notes = "Amigo del gimnasio"
        ))

        ContactoRepository.agregar(Contacto(
            id = "8",
            name = "Lucía Pérez",
            phone = "689012345",
            email = "lucia@gmail.com",
            category = "Estudios",
            notes = "Compañera de clase"
        ))

        ContactoRepository.agregar(Contacto(
            id = "9",
            name = "Miguel Torres",
            phone = "690123456",
            email = null,
            favorite = true,
            category = "Trabajo",
            notes = "Jefe de departamento"
        ))

        ContactoRepository.agregar(Contacto(
            id = "10",
            name = "Sofía Ramírez",
            phone = "601234567",
            email = "sofia@gmail.com",
            category = "Amigos",
            notes = "Amiga de la infancia"
        ))

        ContactoRepository.agregar(Contacto(
            id = "11",
            name = "Alejandro Jiménez",
            phone = "611223344",
            email = "alejandro@gmail.com",
            favorite = true,
            category = "Trabajo",
            notes = "Socio de proyecto"
        ))

        ContactoRepository.agregar(Contacto(
            id = "12",
            name = "Elena Ruiz",
            phone = "622334455",
            email = "elena@gmail.com",
            category = "Familia",
            notes = "Tía"
        ))

        ContactoRepository.agregar(Contacto(
            id = "13",
            name = "Pablo Moreno",
            phone = "633445566",
            email = null,
            category = "Otros",
            notes = "Vecino"
        ))

        ContactoRepository.agregar(Contacto(
            id = "14",
            name = "Claudia Muñoz",
            phone = "644556677",
            email = "claudia@gmail.com",
            favorite = true,
            category = "Amigos",
            notes = "Mejor amiga"
        ))

        ContactoRepository.agregar(Contacto(
            id = "15",
            name = "Javier Alonso",
            phone = "655667788",
            email = "javier@gmail.com",
            category = "Trabajo",
            notes = "Compañero de equipo"
        ))

        ContactoRepository.agregar(Contacto(
            id = "16",
            name = "Isabel Romero",
            phone = "666778899",
            email = "isabel@gmail.com",
            category = "Estudios",
            notes = "Profesora"
        ))

        ContactoRepository.agregar(Contacto(
            id = "17",
            name = "Daniel Navarro",
            phone = "677889900",
            email = null,
            favorite = true,
            category = "Familia",
            notes = "Primo"
        ))

        ContactoRepository.agregar(Contacto(
            id = "18",
            name = "Marta Iglesias",
            phone = "688990011",
            email = "marta@gmail.com",
            category = "Amigos",
            notes = "Amiga del trabajo"
        ))

        ContactoRepository.agregar(Contacto(
            id = "19",
            name = "Adrián Castro",
            phone = "699001122",
            email = "adrian@gmail.com",
            favorite = true,
            category = "Estudios",
            notes = "Compañero de universidad"
        ))

        ContactoRepository.agregar(Contacto(
            id = "20",
            name = "Paula Ortega",
            phone = "600112233",
            email = "paula@gmail.com",
            category = "Otros",
            notes = "Contacto personal"
        ))
        refrescarLista()
    }

    override fun onStart() { super.onStart(); Log.d("MiAgenda", "MainActivity: onStart") }
    override fun onResume() { super.onResume(); Log.d("MiAgenda", "MainActivity: onResume") }
    override fun onPause() { super.onPause(); Log.d("MiAgenda", "MainActivity: onPause") }
    override fun onStop() { super.onStop(); Log.d("MiAgenda", "MainActivity: onStop") }
    override fun onDestroy() { super.onDestroy(); Log.d("MiAgenda", "MainActivity: onDestroy") }
}