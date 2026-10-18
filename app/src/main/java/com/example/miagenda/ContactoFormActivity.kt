package com.example.miagenda

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ContactoFormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contacto_form)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val campoNombre = findViewById<EditText>(R.id.name_input);
        val campoTelefono = findViewById<EditText>(R.id.phone_input);
        val campoEmail = findViewById<EditText>(R.id.email_input);

        val btnAccept = findViewById<Button>(R.id.btnAccept);

        btnAccept.setOnClickListener {

            val nombre = campoNombre.text.toString();
            val telefono = campoTelefono.text.toString();
            val email = campoEmail.text.toString();
            val favorito = findViewById<CheckBox>(R.id.favourite).isChecked();

            var correctForm = true;

            if(nombre.isEmpty()){
                campoNombre.error = getString(R.string.name_error);
                correctForm = false;
            }

            if(!esTelefonoValido(telefono)){
                campoTelefono.error = getString(R.string.phone_error);
                correctForm = false;
            }
            //checkear correcto email o vacio


            if(correctForm) {
                Log.d("MiAgenda", "Formulario correcto: $nombre, $telefono, $email, $favorito")


                //ContactoRepository.agregar(Contacto(nombre, telefono, email, favorito));
                //finish();
            }



        }


        val btnCancel = findViewById<Button>(R.id.btnCancel);

        btnCancel.setOnClickListener {
            finish();
        }
    }

    fun esTelefonoValido(telefono: String): Boolean{

        return telefono.length == 9 && telefono.all { it.isDigit() };
    }
}