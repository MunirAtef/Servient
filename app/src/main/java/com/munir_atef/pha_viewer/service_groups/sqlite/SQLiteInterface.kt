package com.munir_atef.pha_viewer.service_groups.sqlite

import android.content.Context
import com.google.gson.JsonArray
import com.munir_atef.pha_viewer.hosted_file.HostedFileData
import com.munir_atef.pha_viewer.service_groups.ServiceInterface
import com.munir_atef.pha_viewer.service.ServiceResult
import org.json.JSONArray
import org.json.JSONObject


class SQLiteInterface(private val context: Context, private val hostedFileData: HostedFileData): ServiceInterface {
    private val databasePath: String = hostedFileData.manifest.databasePath()
    private var sqlite: SQLiteDb? = null

    override fun invoke(service: String, body: String): ServiceResult {
        if (sqlite == null) sqlite = SQLiteDb(
            context,
            databasePath.split("/").last(),
            hostedFileData.rootPath + "/assets" + databasePath
        )

        return when (service) {
            "insert" -> insertData(body)
            "read" -> readData(body)
            "update" -> update(body)
            "delete" -> delete(body)
            "query" -> rawQuery(body)
            "execute" -> executeSQL(body)

            else -> ServiceResult(null, false)
        }
    }

    fun close() {
        sqlite?.closeConnection()
        sqlite = null
    }

    /**
     * Expected body: {
     *     "table": tableName,
     *     "data": {
     *         "column1": "value1",
     *         "column2": "value2",
     *         "column3": "value3"
     *     }
     * }
     * */
    private fun insertData(body: String): ServiceResult {
        val args = JSONObject(body)
        val tableName: String = args.optString("table")
        val data: JSONObject = args.optJSONObject("data")
            ?: return ServiceResult(null, false)

        val result: Long =  sqlite!!.insertData(tableName = tableName, data = data)
        return ServiceResult(result.toString(), true)
    }

    /**
     * Expected body: {
     *     "table": tableName,
     *     "columns": ["column1", "column2", "column3"],
     *     "where": "condition",
     *     "whereArgs": ["arg1", "arg2"]
     * }
     * */
    private fun readData(body: String): ServiceResult {
        val args = JSONObject(body)

        if (!args.has("table")) return ServiceResult("error".toByteArray(), false)

        val tableName: String = args.optString("table")
        val where: String = args.optString("where")
        val columns: Array<String>? = jsonArrayToArrayString(args.optJSONArray("columns"))
        val whereArgs: Array<String>? = jsonArrayToArrayString(args.optJSONArray("whereArgs"))

        val result: JsonArray = sqlite!!.readData(
            tableName = tableName,
            columns = columns,
            condition = where,
            conditionArgs = whereArgs
        )

        return ServiceResult(result, true)
    }

    /**
     * Expected body: {
     *     "table": tableName,
     *     "data": {
     *         "column1": "value1",
     *         "column2": "value2",
     *         "column3": "value3"
     *     }
     *     "where": "condition",
     *     "whereArgs": ["arg1", "arg2"]
     * }
     * */
    private fun update(body: String): ServiceResult {
        val args = JSONObject(body)

        val tableName: String = args.optString("table")
        val data: JSONObject = args.optJSONObject("data")
            ?: return ServiceResult(null, false)

        val where: String? = if (args.has("where")) args.optString("where") else null
        val whereArgs: Array<String>? = jsonArrayToArrayString(args.optJSONArray("whereArgs"))

        val result: Int =  sqlite!!.update(
            tableName = tableName,
            data = data,
            condition = where,
            conditionArgs = whereArgs
        )

        return ServiceResult(result.toString(), true)
    }

    /**
     * Expected body: {
     *     "table": tableName,
     *     "where": "condition",
     *     "whereArgs": ["arg1", "arg2"]
     * }
     * */
    private fun delete(body: String): ServiceResult {
        val args = JSONObject(body)

        val tableName: String = args.optString("table")
        val where: String? = if (args.has("where")) args.optString("where") else null
        val whereArgs: Array<String>? = jsonArrayToArrayString(args.optJSONArray("whereArgs"))

        val result: Int =  sqlite!!.delete(
            tableName = tableName,
            condition = where,
            conditionArgs = whereArgs
        )

        return ServiceResult(result.toString(), true)
    }

    /**
     * Expected body: {
     *     "sql": sqlStatement,
     *     "args": ["arg1", "arg2"]
     * }
     * */
    private fun rawQuery(body: String): ServiceResult {
        val args = JSONObject(body)

        if (!args.has("sql")) return ServiceResult("error".toByteArray(), false)

        val sql: String = args.optString("sql")
        val sqlArgs: Array<String>? = jsonArrayToArrayString(args.optJSONArray("args"))

        val result: JsonArray = sqlite!!.rawQuery(
            sql = sql,
            args = sqlArgs
        )

        return ServiceResult(result, true)
    }

    /**
     * Expected body: {
     *     "sql": sqlStatement,
     *     "args": ["arg1", "arg2"]
     * }
     * */
    private fun executeSQL(body: String): ServiceResult {
        val args = JSONObject(body)

        if (!args.has("sql")) return ServiceResult(null, false)

        val sql: String = args.optString("sql")
        val sqlArgs: Array<String>? = jsonArrayToArrayString(args.optJSONArray("args"))

        sqlite!!.executeSQL(
            sql = sql,
            args = sqlArgs
        )

        return ServiceResult(null, true)
    }


    private fun jsonArrayToArrayString(jsonArray: JSONArray?): Array<String>? {
        if (jsonArray == null) return null

        val list = mutableListOf<String>()

        for (i: Int in 0 until jsonArray.length()) {
            val value: String = jsonArray.getString(i)
            list.add(value)
        }

        return list.toTypedArray()
    }
}

