package com.example.miagenda

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
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

            val cancelDialogBuilder = AlertDialog.Builder(this);

            cancelDialogBuilder.setMessage(R.string.message_alert)
            .setPositiveButton(R.string.discard){ dialog, id ->
                dialog.dismiss();
                finish();
            }
            .setNegativeButton(R.string.cancel){dialog , id ->
                dialog.dismiss();
            }
        }
    }
}