package com.munir_atef.pha_viewer.service_groups.preferences

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.JsonObject
import java.io.File


class PreferencesDb(context: Context, databaseName: String, private val databasePath: String):
    SQLiteOpenHelper(context, databaseName, null, 1) {

    private val database: SQLiteDatabase = writableDatabase

    override fun onCreate(db: SQLiteDatabase) {}

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    override fun getWritableDatabase(): SQLiteDatabase {
        val databaseFile = File(databasePath)
        return SQLiteDatabase.openOrCreateDatabase(databaseFile, null)
    }

    fun closeConnection() {
        database.close()
    }

    fun setItem(key: String, value: String, type: Int) {
        database.execSQL("INSERT OR REPLACE INTO kvt VALUES (?, ?, ?)", arrayOf(key, value, type))
    }

    fun getItem(key: String): JsonObject {
        val cursor: Cursor = database.rawQuery("SELECT * FROM kvt WHERE id = ?", arrayOf(key))
        val jsonResult = JsonObject()
        if (cursor.count == 0) return jsonResult
        cursor.moveToNext()
        val value = cursor.getString(1)
        when (cursor.getInt(2)) {
            0 -> jsonResult.addProperty(key, value)
            1 -> jsonResult.addProperty(key, value.toInt())
            2 -> jsonResult.addProperty(key, value.toDouble())
        }
        cursor.close()
        return jsonResult
    }

    fun getAll(): JsonObject {
        val cursor: Cursor = database.rawQuery("SELECT * FROM kvt", null)
        val jsonResult = JsonObject()

        if (cursor.count == 0) return jsonResult

        while (cursor.moveToNext()) {
            val key: String = cursor.getString(0)
            val value: String = cursor.getString(1)

            when (cursor.getInt(2)) {
                0 -> jsonResult.addProperty(key, value)
                1 -> jsonResult.addProperty(key, value.toInt())
                2 -> jsonResult.addProperty(key, value.toDouble())
            }
        }

        cursor.close()
        return jsonResult
    }

    fun removeItem(key: String) {
        database.execSQL("DELETE FROM kvt WHERE id = ?", arrayOf(key))
    }

    fun removeAll() {
        database.execSQL("DELETE FROM kvt")
    }
}


