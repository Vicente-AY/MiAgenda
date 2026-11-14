package com.example.miagenda.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AgendaDbHelper(context: Context) : SQLiteOpenHelper(context, "agenda.db", null, 4) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE contactos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "phone TEXT NOT NULL, " +
                "email TEXT, " +
                "favourite INTEGER, " +
                "category TEXT NOT NULL, " +
                "notes TEXT," +
                "birthdate TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        if(oldVersion < 3){
            db.execSQL("ALTER TABLE contactos ADD COLUMN birthdate TEXT")
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE contactos DROP COLUMN birthday")
        }


    }

}