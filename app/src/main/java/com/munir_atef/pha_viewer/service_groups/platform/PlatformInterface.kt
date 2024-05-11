package com.munir_atef.pha_viewer.service_groups.platform

import android.os.Build
import android.os.Environment.*
import com.google.gson.JsonObject
import com.munir_atef.pha_viewer.service_groups.ServiceInterface
import com.munir_atef.pha_viewer.service.ServiceResult


object PlatformInterface: ServiceInterface {
    override fun invoke(service: String, body: String): ServiceResult {
        val result: ServiceResult = when (service) {
            "info"-> platformInfo()
            "standard-paths" -> standardPaths()
            "external-directory" -> externalDirectory()
            "downloads-directory" -> downloadsDirectory()
            else -> ServiceResult(null, false)
        }

        return result
    }


    private fun platformInfo(): ServiceResult {
        val info = JsonObject()
        info.addProperty("osName", "android")
        info.addProperty("osVersion", Build.VERSION.RELEASE)
        info.addProperty("deviceModel", Build.MODEL)
        info.addProperty("manufacturer", Build.MANUFACTURER)
        info.addProperty("separator", "/")
        info.addProperty("pathSeparator", ":")

        return ServiceResult(info, true)
    }

    private fun standardPaths(): ServiceResult {
        val externalStorage: String = getExternalStorageDirectory().path

        val info = JsonObject()
        info.addProperty(DIRECTORY_ALARMS, "$externalStorage/$DIRECTORY_ALARMS")
        info.addProperty(DIRECTORY_PICTURES, "$externalStorage/$DIRECTORY_PICTURES")
        info.addProperty(DIRECTORY_DOCUMENTS, "$externalStorage/$DIRECTORY_DOCUMENTS")
        info.addProperty(DIRECTORY_DOWNLOADS, "$externalStorage/$DIRECTORY_DOWNLOADS")
        info.addProperty(DIRECTORY_DCIM, "$externalStorage/$DIRECTORY_DCIM")
        info.addProperty(DIRECTORY_MOVIES, "$externalStorage/$DIRECTORY_MOVIES")
        info.addProperty(DIRECTORY_MUSIC, "$externalStorage/$DIRECTORY_MUSIC")
        info.addProperty(DIRECTORY_NOTIFICATIONS, "$externalStorage/$DIRECTORY_NOTIFICATIONS")
        info.addProperty(DIRECTORY_RINGTONES, "$externalStorage/$DIRECTORY_RINGTONES")

        return ServiceResult(info, true)
    }

    private fun externalDirectory(): ServiceResult {
        val externalDir = getExternalStorageDirectory().path
        return ServiceResult(externalDir, true)
    }

    private fun downloadsDirectory(): ServiceResult {
        val downloadsDir = "${getExternalStorageDirectory().path}/$DIRECTORY_DOWNLOADS"
        return ServiceResult(downloadsDir, true)
    }
}

/**
 * static files: src
 *
 * */


