package com.munir_atef.pha_viewer.service_groups.sqlite


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONObject
import java.io.File


class SQLiteDb(context: Context, databaseName: String, private val databasePath: String):
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

    private fun blobToJsonArray(blob: ByteArray): JsonArray {
        val jsonArray = JsonArray()
        for (i: Int in blob.indices) {
            jsonArray.add(blob[i].toInt())
        }
        return jsonArray
    }


    private fun cursorToJson(cursor: Cursor): JsonArray {
        val jsonArray = JsonArray()

        if (cursor.count == 0) return jsonArray

        cursor.moveToNext()
        val columnCount: Int = cursor.columnCount
        val columnNames: List<String> = cursor.columnNames.asList()
        val columnTypes: MutableList<Int> = mutableListOf()

        fun cursorToObject(cursor: Cursor): JsonObject {
            val jsonObject = JsonObject()
            for (i in 0 until columnCount) {
                when(columnTypes[i]) {
                    Cursor.FIELD_TYPE_STRING -> jsonObject.addProperty(columnNames[i], cursor.getString(i))
                    Cursor.FIELD_TYPE_INTEGER -> jsonObject.addProperty(columnNames[i], cursor.getInt(i))
                    Cursor.FIELD_TYPE_FLOAT -> jsonObject.addProperty(columnNames[i], cursor.getFloat(i))
                    Cursor.FIELD_TYPE_NULL -> jsonObject.add(columnNames[i], null)
                    Cursor.FIELD_TYPE_BLOB -> jsonObject.add(columnNames[i], blobToJsonArray(cursor.getBlob(i)))
                }
            }

            return jsonObject
        }

        for (i in 0 until columnCount) columnTypes.add(cursor.getType(i))

        jsonArray.add(cursorToObject(cursor))
        while (cursor.moveToNext()) jsonArray.add(cursorToObject(cursor))
        cursor.close()

        return jsonArray
    }


    fun insertData(tableName: String, data: JSONObject): Long {
        val values = ContentValues()

        data.keys().forEach { key: String ->
            when (val value = data[key]) {
                is String? -> values.put(key, value)
                is Int? -> values.put(key, value)
                is Double? -> values.put(key, value)
                is Long? -> values.put(key, value)
            }
        }

        return database.insert(tableName, null, values)
    }

    fun readData(
        tableName: String,
        columns: Array<String>?,
        condition: String?,
        conditionArgs: Array<String>?
    ): JsonArray {
        val cursor: Cursor = database.query(
            tableName,
            columns,
            condition,
            conditionArgs,
            null, null, null
        )

        return cursorToJson(cursor)
    }

    fun rawQuery(sql: String, args: Array<String>?): JsonArray {
        val cursor: Cursor = database.rawQuery(sql, args)
        return cursorToJson(cursor)
    }

    fun delete(tableName: String, condition: String?, conditionArgs: Array<String>?): Int {
        return database.delete(tableName, condition, conditionArgs)
    }

    fun update(tableName: String, data: JSONObject, condition: String?, conditionArgs: Array<String>?): Int {
        val values = ContentValues()

        data.keys().forEach { key: String ->
            when (val value = data[key]) {
                is String? -> values.put(key, value)
                is Int? -> values.put(key, value)
                is Double? -> values.put(key, value)
                is Long? -> values.put(key, value)
            }
        }

        return database.update(tableName, values, condition, conditionArgs)
    }

    fun executeSQL(sql: String?, args: Array<String>?) {
        database.execSQL(sql, args)
    }
}


