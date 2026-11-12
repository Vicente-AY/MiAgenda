package com.example.miagenda

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ContactoDetalleActivity : AppCompatActivity() {

    private var favorito: Boolean = false;

    private val launcherEditar = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            resultado ->
        if(resultado.resultCode == RESULT_OK){
            val datos = resultado.data;

            if(datos != null){
                val id = datos.getStringExtra("id") ?: "";
                val nombre = datos.getStringExtra("nombre") ?: "";
                val telefono = datos.getStringExtra("telefono") ?: "";
                val email = datos.getStringExtra("email");
                favorito = datos.getBooleanExtra("favorito", false);
                val categoria = datos.getStringExtra("category") ?: "";
                val notas = datos.getStringExtra("notes");

                val contacto = Contacto(id, nombre, telefono, email, favorito, categoria, notas);

                RepositoryProvider.get(this).guardar(contacto, {

                    findViewById<TextView>(R.id.name).text = nombre;
                    findViewById<TextView>(R.id.phone).text = telefono;
                    findViewById<TextView>(R.id.email).text = email;
                    if(favorito) findViewById<TextView>(R.id.fav).text = getString(R.string.is_favourite) else findViewById<TextView>(R.id.fav).text = getString(R.string.not_favourite);
                    findViewById<TextView>(R.id.category).text = categoria
                    findViewById<TextView>(R.id.notes).text = notas;
                    findViewById<Button>(R.id.btnEmail).isEnabled = !email.isNullOrEmpty();
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
        setContentView(R.layout.activity_contacto_detalle)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //campos
        val campoNombre = findViewById<TextView>(R.id.name);
        val campoTelefono = findViewById<TextView>(R.id.phone);
        val campoEmail = findViewById<TextView>(R.id.email);
        val campoFavorito = findViewById<TextView>(R.id.fav);
        val campoCategoria = findViewById<TextView>(R.id.category);
        val campoNotas = findViewById<TextView>(R.id.notes);

        //id
        val id: String = intent.getStringExtra("id") ?: "";

        //datos
        val nombre = intent.getStringExtra("nombre") ?: "";
        campoNombre.setText(nombre)

        val telefono = intent.getStringExtra("telefono") ?: "";
        campoTelefono.setText(telefono);

        val email = intent.getStringExtra("email");
        campoEmail.setText(email)

        favorito = intent.getBooleanExtra("favorito", false);
        if(favorito) campoFavorito.setText(R.string.is_favourite) else campoFavorito.setText(R.string.not_favourite);

        val categoria = intent.getStringExtra("categoria") ?: "Otros";
        campoCategoria.setText(categoria);

        val notas = intent.getStringExtra("notas");
        campoNotas.setText(notas);

        //botones
        val btnCall = findViewById<Button>(R.id.btnCall);
        val btnEmail = findViewById<Button>(R.id.btnEmail);
        val btnEdit = findViewById<Button>(R.id.btnEdit);
        val btnDelete = findViewById<Button>(R.id.btnDelete);

        btnCall.setOnClickListener {
            realizarLlamada(campoTelefono.text.toString())
        }

        if(campoEmail.text.toString() == "") btnEmail.isEnabled = false else btnEmail.isEnabled = true;

        btnEmail.setOnClickListener {
            enviarEmail(campoEmail.text.toString())
        }

        btnEdit.setOnClickListener {

            val intent = Intent(this, ContactoFormActivity::class.java)
            intent.putExtra("id", id);
            intent.putExtra("nombre", campoNombre.text.toString());
            intent.putExtra("telefono", campoTelefono.text.toString());
            intent.putExtra("email", campoEmail.text.toString());
            intent.putExtra("favorito", favorito);
            intent.putExtra("categoria", campoCategoria.text.toString());
            intent.putExtra("notas", campoNotas.text.toString());

            launcherEditar.launch(intent);
        }

        btnDelete.setOnClickListener {

            val settings = getSharedPreferences("settings", MODE_PRIVATE);
            val mostrarCuadro = settings.getBoolean("settings_remove", false)

            if (mostrarCuadro) {

                AlertDialog.Builder(this)
                    .setMessage(getString(R.string.delete_message_alert) + " " + nombre + "?")
                    .setPositiveButton(R.string.btn_delete) { _, _ ->
                        eliminarContacto(id);
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show();
            }
            else{
                eliminarContacto(id);
            }

        }

        var btnAtras = findViewById<Button>(R.id.btnAtras);
        btnAtras.setOnClickListener {
            finish()
        }
    }


    private fun eliminarContacto(id: String){

        RepositoryProvider.get(this).eliminar(
            id,
            onOk = { finish() },
            onError = { error ->
                Toast.makeText(
                    this,
                    error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

    }

    private fun realizarLlamada(phone: String){

        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = "tel:${phone}".toUri()

        startActivity(intent);
    }

    private fun enviarEmail(email: String){

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = "mailto:${email}".toUri()

        startActivity(intent);
    }


    override fun onStart() { super.onStart(); Log.d("MiAgenda", "ContactoDetalleActivity: onStart") }
    override fun onResume() { super.onResume(); Log.d("MiAgenda", "ContactoDetalleActivity: onResume") }
    override fun onPause() { super.onPause(); Log.d("MiAgenda", "ContactoDetalleActivity: onPause") }
    override fun onStop() { super.onStop(); Log.d("MiAgenda", "ContactoDetalleActivity: onStop") }
    override fun onDestroy() { super.onDestroy(); Log.d("MiAgenda", "ContactoDetalleActivity: onDestroy") }

}