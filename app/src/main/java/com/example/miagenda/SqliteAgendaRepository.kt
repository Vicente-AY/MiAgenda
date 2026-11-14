package com.example.miagenda

import android.content.ContentValues
import android.content.Context
import android.icu.text.MessagePattern

class SqliteAgendaRepository(context: Context) : AgendaRepository {

    private val dbHelper = AgendaDbHelper(context);

    override fun obtenerTodos(onOk: (List<Contacto>) -> Unit, onError: (String) -> Unit) {

        try {
            val lista = mutableListOf<Contacto>();
            val db = dbHelper.readableDatabase;

            db.query("contactos", null, null, null, null, null, null).use { cursor ->

                val idCol = cursor.getColumnIndexOrThrow("id");
                val nameCol = cursor.getColumnIndexOrThrow("name");
                val phoneCol = cursor.getColumnIndexOrThrow("phone");
                val emailCol = cursor.getColumnIndexOrThrow("email");
                val favouriteCol = cursor.getColumnIndexOrThrow("favourite");
                val categoryCol = cursor.getColumnIndexOrThrow("category");
                val notasCol = cursor.getColumnIndexOrThrow("notes");

                while (cursor.moveToNext()) {
                    val contacto = Contacto(
                        id = cursor.getInt(idCol).toString(),
                        name = cursor.getString(nameCol),
                        phone = cursor.getString(phoneCol),
                        email = cursor.getString(emailCol),
                        favorite = cursor.getInt(favouriteCol) == 1,
                        category = cursor.getString(categoryCol),
                        notes = cursor.getString(notasCol)
                    );

                    lista.add(contacto);
                }
            }

            onOk(lista);

        }
        catch(e: Exception){
            onError(e.message ?: "Error error al obtener contactos");
        }

    }

    override fun buscar(text: String, onOk: (List<Contacto>) -> Unit, onError: (String) -> Unit) {

        try {
            val lista = mutableListOf<Contacto>();
            val db = dbHelper.readableDatabase;

            val busqueda = "name LIKE ?";
            val args = arrayOf("%$text%");

            db.query("contactos", null, busqueda, args, null, null, null).use { cursor ->

                val idCol = cursor.getColumnIndexOrThrow("id");
                val nameCol = cursor.getColumnIndexOrThrow("name");
                val phoneCol = cursor.getColumnIndexOrThrow("phone");
                val emailCol = cursor.getColumnIndexOrThrow("email");
                val favouriteCol = cursor.getColumnIndexOrThrow("favourite");
                val categoryCol = cursor.getColumnIndexOrThrow("category");
                val notasCol = cursor.getColumnIndexOrThrow("notes");

                while (cursor.moveToNext()) {

                    val contacto = Contacto(
                        id = cursor.getInt(idCol).toString(),
                        name = cursor.getString(nameCol),
                        phone = cursor.getString(phoneCol),
                        email = cursor.getString(emailCol),
                        favorite = cursor.getInt(favouriteCol) == 1,
                        category = cursor.getString(categoryCol),
                        notes = cursor.getString(notasCol)
                    );

                    lista.add(contacto);

                }
            }

            onOk(lista);

        }
        catch(e: Exception) {
            onError(e.message ?: "Error al buscar contacto");
        }
    }

    override fun obtenerPorId(id: String, onOk: (Contacto?) -> Unit, onError: (String) -> Unit) {

        try{

            var contacto: Contacto? = null;
            val db = dbHelper.readableDatabase;

            db.query("contactos", null, "id = ?", arrayOf(id), null, null, null).use { cursor ->

                val idCol = cursor.getColumnIndexOrThrow("id");
                val nameCol = cursor.getColumnIndexOrThrow("name");
                val phoneCol = cursor.getColumnIndexOrThrow("phone");
                val emailCol = cursor.getColumnIndexOrThrow("email");
                val favouriteCol = cursor.getColumnIndexOrThrow("favourite");
                val categoryCol = cursor.getColumnIndexOrThrow("category");
                val notasCol = cursor.getColumnIndexOrThrow("notes");

                if (cursor.moveToFirst()) {

                    contacto = Contacto(
                        id = cursor.getInt(idCol).toString(),
                        name = cursor.getString(nameCol),
                        phone = cursor.getString(phoneCol),
                        email = cursor.getString(emailCol),
                        favorite = cursor.getInt(favouriteCol) == 1,
                        category = cursor.getString(categoryCol),
                        notes = cursor.getString(notasCol)
                    );
                }
            }

            onOk(contacto);

        }
        catch(e: Exception){
            onError(e.message ?: "Error obtener contacto");
        }

    }

    override fun guardar(contacto: Contacto, onOk: () -> Unit, onError: (String) -> Unit) {

        try{

            val db = dbHelper.writableDatabase;

            val valores = ContentValues().apply {
                put("name", contacto.name);
                put("phone", contacto.phone);
                put("email", contacto.email);
                put("favourite", contacto.favorite);
                put("category", contacto.category);
                put("notes", contacto.notes);
            }

            if(contacto.id.isEmpty()){

                db.insertOrThrow("contactos",
                    null,
                    valores
                );

            }
            else{

                db.update("contactos",
                    valores,
                    "id = ?",
                    arrayOf(contacto.id)
                );

            }

            onOk();

        }
        catch(e: Exception){
            onError(e.message ?: "Error al guardar");
        }
    }

    override fun eliminar(id: String, onOk: () -> Unit, onError: (String) -> Unit) {

        try{

            val db = dbHelper.writableDatabase;

            db.delete("contactos",
                "id = ?",
                arrayOf(id)
            );

            onOk();

        }
        catch(e: Exception){
            onError(e.message ?: "Error al eliminar");
        }

    }

    override public fun obtenerNumeroDeContactosPorCategoria(categoria: String, onOk: (Int) -> Unit, onError: (String) -> Unit){

        try{
            val db = dbHelper.readableDatabase;
            val sql = "SELECT COUNT(*) FROM contactos WHERE category = ?";

            db.rawQuery(sql, arrayOf(categoria)).use { cursor ->

                var total = 0;
                if (cursor.moveToFirst()) {
                    total = cursor.getInt(0);
                }

                onOk(total);
            }

        }
        catch(e: Exception){
            onError(e.message ?: "Error al obtener contactos por categoria");
        }

    }
}