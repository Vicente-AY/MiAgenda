package com.example.miagenda

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AgendaDbHelper(context: Context) : SQLiteOpenHelper(context, "agenda.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE contactos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "phone TEXT NOT NULL, " +
                "email TEXT, " +
                "favourite INTEGER, " +
                "category TEXT NOT NULL, " +
                "notes TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //??
    }

    fun insertarContacto(name: String, phone: String, email: String?, favourite: Boolean, category: String, notes: String?): Long {

        val db = this.writableDatabase;
        val valores = ContentValues().apply {
            put("name", name)
            put("phone", phone)
            put("email", email)
            put("favourite", favourite)
            put("category", category)
            put("notes", notes)

        }
        return db.insertOrThrow("contactos", null, valores)

    }

    fun actualizarContacto(id: Int, name: String, phone: String, email: String?, favourite: Boolean, category: String, notes: String?): Int {

        val db = this.writableDatabase;

        var valores = ContentValues().apply {
            put("name", name)
            put("phone", phone)
            put("email", email)
            put("favourite", favourite)
            put("category", category)
            put("notes", notes)
        }

        return db.update("contactos", valores, "id = ?", arrayOf(id.toString()));

    }

    fun eliminarContacto(id: Int): Int {

        val db = this.writableDatabase;
        return db.delete("contactos", "id = ?", arrayOf(id.toString()));

    }


}