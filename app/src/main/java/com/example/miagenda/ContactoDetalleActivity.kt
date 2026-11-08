package com.example.miagenda

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ContactoDetalleActivity : AppCompatActivity() {
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
        val nombre = intent.getStringExtra("name") ?: "";
        campoNombre.setText(nombre)

        val telefono = intent.getStringExtra("phone") ?: "";
        campoTelefono.setText(telefono);

        val email = intent.getStringExtra("email");
        campoEmail.setText(email)

        val favorito = intent.getBooleanExtra("favourite", false);
        if(favorito) campoFavorito.setText(R.string.is_favourite) else campoFavorito.setText(R.string.not_favourite);

        val categoria = intent.getStringExtra("category") ?: "";
        campoCategoria.setText(categoria);

        val notas = intent.getStringExtra("notes");
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
            intent.putExtra(id, id);
            intent.putExtra("name", campoNombre.text.toString());
            intent.putExtra("phone", campoTelefono.text.toString());
            intent.putExtra("email", campoEmail.text.toString());
            intent.putExtra("favourite", favorito);
            intent.putExtra("category", campoCategoria.text.toString());
            intent.putExtra("notes", campoNotas.text.toString());

            startActivity(intent);
        }

        btnDelete.setOnClickListener {

            var contacto = ContactoRepository.getContactoById(id);

            if(contacto != null){

                AlertDialog.Builder(this)
                    .setMessage(getString(R.string.delete_message_alert) + " " + nombre + "?")
                    .setPositiveButton(R.string.btn_delete) { _, _ ->
                        ContactoRepository.eliminar(contacto)
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show();

            }
        }
    }

    private fun realizarLlamada(phone: String){

        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = "tel:${phone}".toUri()

        startActivity(intent);
    }

    private fun enviarEmail(email: String){

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = "mailto:${email}".toUri()

    }
}