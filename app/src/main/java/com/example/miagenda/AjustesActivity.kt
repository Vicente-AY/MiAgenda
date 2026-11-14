package com.example.miagenda

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AjustesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ajustes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val order = findViewById<Spinner>(R.id.order);
        val eliminar = findViewById<CheckBox>(R.id.remove_policy);

        //cargamos las opciones previas
        val settings = getSharedPreferences("settings", MODE_PRIVATE);
        val previousOrder = settings.getString("settings_order", "");
        val previousRemove = settings.getBoolean("settings_remove", true);

        order.setSelection(resources.getStringArray(R.array.contact_order).indexOf(previousOrder));

        eliminar.isChecked = previousRemove;

        val btnAceptar = findViewById<Button>(R.id.btnSaveSettings);
        btnAceptar.setOnClickListener {

            val ordenElegido = order.selectedItem.toString();
            val politicaEliminacion = eliminar.isChecked();

            val saveDialogBuilder = AlertDialog.Builder(this);

            saveDialogBuilder.setMessage(R.string.save_settings_alert)
                .setPositiveButton(R.string.save_settings){ dialog, id ->

                    getSharedPreferences("settings", MODE_PRIVATE)
                        .edit()
                        .putString("settings_order", ordenElegido)
                        .putBoolean("settings_remove",politicaEliminacion)
                        .apply();

                    dialog.dismiss();
                    finish();
                }
                .setNegativeButton(R.string.cancel){dialog , id ->
                    dialog.dismiss();
                }

            val saveDialog: AlertDialog = saveDialogBuilder.create();
            saveDialog.show();

        }

        val btnCancelar = findViewById<Button>(R.id.btnCancelSettings);
        btnCancelar.setOnClickListener {

            finish();
        }

        cargarResumen();

    }

    private fun cargarResumen(){

        val progressBar = findViewById<ProgressBar>(R.id.loading);
        progressBar.visibility = View.VISIBLE;

        RepositoryProvider.get(this).obtenerNumeroDeContactosPorCategoria("Otros",
            onOk = { total ->

                findViewById<TextView>(R.id.other_count).text = total.toString();

            },
            onError = { error ->

                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()

            }
        );

        RepositoryProvider.get(this).obtenerNumeroDeContactosPorCategoria("Trabajo",
            onOk = { total ->

                findViewById<TextView>(R.id.work_count).text = total.toString();

            },
            onError = { error ->

                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()

            }
        );

        RepositoryProvider.get(this).obtenerNumeroDeContactosPorCategoria("Amigos",
            onOk = { total ->

                findViewById<TextView>(R.id.friends_count).text = total.toString();

            },
            onError = { error ->

                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()

            }
        );

        RepositoryProvider.get(this).obtenerNumeroDeContactosPorCategoria("Estudios",
            onOk = { total ->

                findViewById<TextView>(R.id.studies_count).text = total.toString();

            },
            onError = { error ->

                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()

            }
        );

        RepositoryProvider.get(this).obtenerNumeroDeContactosPorCategoria("Familia",
            onOk = { total ->

                findViewById<TextView>(R.id.family_count).text = total.toString();

            },
            onError = { error ->

                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();

            }
        );

        RepositoryProvider.get(this).obtenerTodos(
            onOk = { lista ->
                val total = lista.size

                if(lista.size <= 0){
                    findViewById<TextView>(R.id.total_count).text = "0";
                }
                else{
                    findViewById<TextView>(R.id.total_count).text = total.toString();
                }
            },
            {mensaje ->
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();

            }
        );

        progressBar.visibility = View.GONE;

    }


    override fun onStart() { super.onStart(); Log.d("MiAgenda", "AjustesActivity: onStart") }
    override fun onResume() {
        super.onResume();
        Log.d("MiAgenda", "AjustesActivity: onResume")
        cargarResumen()
    }
    override fun onPause() { super.onPause(); Log.d("MiAgenda", "AjustesActivity: onPause") }
    override fun onStop() { super.onStop(); Log.d("MiAgenda", "AjustesActivity: onStop") }
    override fun onDestroy() { super.onDestroy(); Log.d("MiAgenda", "AjustesActivity: onDestroy") }

}