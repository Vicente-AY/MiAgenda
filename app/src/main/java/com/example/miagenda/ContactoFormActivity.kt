package com.example.miagenda

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ContactoFormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MiAgenda", "ContactoFormActivity: onCreate")
        enableEdgeToEdge()
        setContentView(R.layout.activity_contacto_form)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //campos
        val campoNombre = findViewById<EditText>(R.id.name_input);
        val campoTelefono = findViewById<EditText>(R.id.phone_input);
        val campoEmail = findViewById<EditText>(R.id.email_input);
        val campoFavorito = findViewById<CheckBox>(R.id.favourite)
        val campoCategoria = findViewById<Spinner>(R.id.category);
        val campoNotas = findViewById<EditText>(R.id.notes_input);

        val id = intent.getStringExtra("id")
        if(!id.isNullOrEmpty() && savedInstanceState == null){
            campoNombre.setText(intent.getStringExtra("nombre"))
            campoTelefono.setText(intent.getStringExtra("telefono"))
            campoEmail.setText(intent.getStringExtra("email"))
            campoFavorito.isChecked = intent.getBooleanExtra("favorito", false)
            campoCategoria.setSelection(resources.getStringArray(R.array.categories).indexOf(intent.getStringExtra("category")))
            campoNotas.setText(intent.getStringExtra("notes"))

            findViewById<CheckBox>(R.id.favourite).isChecked = intent.getBooleanExtra("favorito", false)
            findViewById<TextView>(R.id.title).text = getString(R.string.edit_contact_form_title)
        }
        val btnAccept = findViewById<Button>(R.id.btnAccept);
        btnAccept.setOnClickListener {

            var nombre = campoNombre.text.toString().trim();
            var telefono = campoTelefono.text.toString().trim();
            var email = campoEmail.text.toString().trim();
            var favorito = campoFavorito.isChecked();
            var categoria = campoCategoria.selectedItem.toString();
            var notas = campoNotas.text.toString().trim();

            var correctForm = true;

            if(nombre.isEmpty()){
                campoNombre.error = getString(R.string.name_error);
                correctForm = false;
            }

            if(!esTelefonoValido(telefono)){
                campoTelefono.error = getString(R.string.phone_error);
                correctForm = false;
            }

            if(!checkEmail(email)){
                campoEmail.error = getString(R.string.email_error);
                correctForm = false;
            }


            if(correctForm) {

                val resultado = Intent();

                resultado.putExtra("id", id ?: "");
                resultado.putExtra("nombre", nombre);
                resultado.putExtra("telefono", telefono);
                resultado.putExtra("email", email.ifEmpty { null });
                resultado.putExtra("favorito", favorito);
                resultado.putExtra("category", categoria);
                resultado.putExtra("notes", notas.ifEmpty { null });

                setResult(RESULT_OK, resultado);

                Log.d("MiAgenda", "Contacto añadido $nombre $telefono $email $favorito $categoria $notas")

                finish();
            }

        }

        val btnCancel = findViewById<Button>(R.id.btnCancel);


        btnCancel.setOnClickListener {

            val nombre = campoNombre.text.toString().trim();
            val telefono = campoTelefono.text.toString().trim();
            val email = campoEmail.text.toString().trim();

            if(!nombre.isEmpty() || !telefono.isEmpty() || !email.isEmpty()){

                val cancelDialogBuilder = AlertDialog.Builder(this);

                cancelDialogBuilder.setMessage(R.string.message_alert)
                    .setPositiveButton(R.string.discard){ dialog, id ->
                        dialog.dismiss();
                        finish();
                    }
                    .setNegativeButton(R.string.cancel){dialog , id ->
                        dialog.dismiss();
                    }

                val cancelDialog: AlertDialog = cancelDialogBuilder.create();

                cancelDialog.show();
            }
            else{
                finish();
            }
        }
    }

    fun esTelefonoValido(telefono: String): Boolean{

        return telefono.length == 9 && telefono.all { it.isDigit() };
    }

    fun checkEmail(email: String): Boolean{

        if(email.isEmpty()) return true;

        var atIndex: Int = email.indexOf('@');
        var dotIndex: Int = email.indexOf('.');

        return atIndex > 0 && //arroba no es el primer caracter
                dotIndex > atIndex + 1 && //punto esta despues que el arroba y deja un margen de 1 caracher
                dotIndex < email.length - 2;//punto no es el ultimo caracter y deja un espacio de almenos 2 caracteres


    }

    override fun onStart() { super.onStart(); Log.d("MiAgenda", "ContactoFormActivity: onStart") }
    override fun onResume() { super.onResume(); Log.d("MiAgenda", "ContactoFormActivity: onResume") }
    override fun onPause() { super.onPause(); Log.d("MiAgenda", "ContactoFormActivity: onPause") }
    override fun onStop() { super.onStop(); Log.d("MiAgenda", "ContactoFormActivity: onStop") }
    override fun onDestroy() { super.onDestroy(); Log.d("MiAgenda", "ContactoFormActivity: onDestroy") }

}
