package com.munir_atef.pha_viewer.service_groups.preferences

import android.content.Context
import com.google.gson.JsonObject
import com.munir_atef.pha_viewer.hosted_file.HostedFileData
import com.munir_atef.pha_viewer.service_groups.ServiceInterface
import com.munir_atef.pha_viewer.service.ServiceResult
import org.json.JSONObject

/** CREATE TABLE kvt (id TEXT PRIMARY KEY, value TEXT NOT NULL, type INTEGER NOT NULL) */

class PreferencesInterface(private val context: Context, private val hostedFileData: HostedFileData): ServiceInterface {
    private val preferencesPath: String = hostedFileData.manifest.preferencesPath()
    private var preferences: PreferencesDb? = null

    override fun invoke(service: String, body: String): ServiceResult {
        if (preferences == null) preferences = PreferencesDb(
            context,
            preferencesPath.split("/").last(),
            hostedFileData.rootPath + "/assets" + preferencesPath
        )

        val result: ServiceResult = when (service) {
            "set-item" -> setItem(body)
            "get-item" -> getItem(body)
            "remove-item" -> removeItem(body)
            "get-all-items" -> getAllItems()
            "remove-all-items" -> removeAllItems()

            else -> ServiceResult(null, false)
        }

        return result
    }

    fun close() {
        preferences?.closeConnection()
        preferences = null
    }


    /**
     * Expected body: {
     *     "key": key,
     *     "value": value
     * }
     * */
    private fun setItem(body: String): ServiceResult {
        val args = JSONObject(body)
        val key: String = args.optString("key")

        when (val value: Any? = args.opt("value")) {
            is String -> preferences!!.setItem(key, value, 0)
            is Int -> preferences!!.setItem(key, value.toString(), 1)
            is Double -> preferences!!.setItem(key, value.toString(), 2)
            else -> return ServiceResult(null, false)
        }

        return ServiceResult(null, true)
    }

    /**
     * Expected body: key
     * */
    private fun getItem(body: String): ServiceResult {
        val item: JsonObject = preferences!!.getItem(body)
        return ServiceResult(item, true)
    }

    /**
     * Expected body: key
     * */
    private fun removeItem(body: String): ServiceResult {
        preferences!!.removeItem(body)
        return ServiceResult(null, true)
    }

    private fun getAllItems(): ServiceResult {
        val items: JsonObject = preferences!!.getAll()
        return ServiceResult(items, true)
    }

    private fun removeAllItems(): ServiceResult {
        preferences!!.removeAll()
        return ServiceResult(null, true)
    }
}

