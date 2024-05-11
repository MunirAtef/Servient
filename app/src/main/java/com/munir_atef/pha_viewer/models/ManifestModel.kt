package com.munir_atef.pha_viewer.models

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.File

class ManifestModel {
    private lateinit var name: String
    private lateinit var launch: String
    private lateinit var database: String
    private lateinit var preferences: String
    private var requiredServices: List<String>? = null
    private var icon: AppIconModel? = null

    constructor(manifestPath: String) {
        val manifestText: String = File(manifestPath).readText()
        println("manifestText from path: \n$manifestText")
        initializeData(manifestText)
    }

    constructor(manifestBytes: ByteArray) {
        val manifestText: String = manifestBytes.decodeToString()
        println("manifestText from bytes: \n$manifestText")
        initializeData(manifestText)
    }

    private fun initializeData(manifestText: String) {
        val manifestJson: JsonObject = Gson().fromJson(manifestText, JsonObject::class.java)

        name = manifestJson.get("name")?.asString ?: ""
        launch = manifestJson.get("launch")?.asString ?: ""
        database = manifestJson.get("databasePath")?.asString ?: ""
        preferences = manifestJson.get("preferencesPath")?.asString ?: ""
        icon = AppIconModel(manifestJson.get("icon")?.asJsonObject)

        val services: JsonArray? = manifestJson.get("requiredServices")?.asJsonArray

        if (services != null) {
            requiredServices = (0 until services.size()).map { services.get(it).asString }.toList()
        }
    }


    fun appName(): String = name
    fun launchFile(): String = "/src$launch"
    fun databasePath(): String = database
    fun preferencesPath(): String = preferences
    fun permissions(): List<String>? = requiredServices
    fun appIcon(): AppIconModel? = icon
}


