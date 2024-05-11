package com.munir_atef.pha_viewer.models

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.munir_atef.pha_viewer.shared.SharedData
import java.io.File

class SavedFileModel {
    var appName: String? = null
    var folderName: String? = null
    var externalZipFile: String? = null
    var lastOpened: Long = 0
    var autoStart: Boolean = false
    var grantedPermission: MutableSet<String> = mutableSetOf()
    var appIcon: AppIconModel? = null

    constructor()

    constructor(jsonString: String) {
        val jsonObject = Gson().fromJson(jsonString, JsonObject::class.java)

        appName = jsonObject.get("name")?.asString
        folderName = jsonObject.get("folder")?.asString
        externalZipFile = jsonObject.get("externalPath")?.asString
        lastOpened = jsonObject.get("lastOpened")?.asLong ?: 0
        autoStart = jsonObject.get("autoStart")?.asBoolean ?: false
        appIcon = AppIconModel(jsonObject.get("icon")?.asJsonObject)

        jsonToList(jsonObject.get("grantedPermission")?.asJsonArray)
        println("Folder Name: $folderName, Opened: $lastOpened")
    }

    fun toJsonString(): String {
        val jsonObject = JsonObject()
        jsonObject.addProperty("name", appName)
        jsonObject.addProperty("folder", folderName)
        jsonObject.addProperty("externalPath", externalZipFile)
        jsonObject.addProperty("lastOpened", lastOpened)
        jsonObject.addProperty("autoStart", autoStart)
        jsonObject.add("grantedPermission", listToJson())
        jsonObject.add("icon", appIcon?.iconAsJson())

        return jsonObject.toString()
    }

    fun writeFile(updateLastOpened: Boolean) {
        if (updateLastOpened) lastOpened = System.currentTimeMillis()
        File("${SharedData.rootForUnzipped}/data/$folderName.json").writeText(toJsonString())
    }

    private fun listToJson(): JsonArray {
        val jsonArray = JsonArray()
        grantedPermission.forEach { jsonArray.add(it) }
        return jsonArray
    }

    private fun jsonToList(jsonArray: JsonArray?) {
        grantedPermission = jsonArray?.map { it.asString }?.toMutableSet() ?: mutableSetOf()
    }

    override fun toString(): String {
        return """
            {
                "name": $appName,
                "folder": $folderName,
                "externalPath": $externalZipFile,
                "lastOpened": $lastOpened,
                "autoStart": $autoStart,
                "grantedPermission": $grantedPermission
                "icon": $appIcon
            }
        """.trimIndent()
    }
}

