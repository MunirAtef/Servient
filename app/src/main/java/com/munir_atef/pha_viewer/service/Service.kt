package com.munir_atef.pha_viewer.service

import android.content.Context
import com.munir_atef.pha_viewer.hosted_file.HostedFileData
import com.munir_atef.pha_viewer.service_groups.ServiceInterface
import com.munir_atef.pha_viewer.service_groups.filesystem.FilesystemInterface
import com.munir_atef.pha_viewer.service_groups.platform.PlatformInterface
import com.munir_atef.pha_viewer.service_groups.preferences.PreferencesInterface
import com.munir_atef.pha_viewer.service_groups.sqlite.SQLiteInterface

class Service(context: Context, hostedFileData: HostedFileData) {
    private var sqlite: SQLiteInterface = SQLiteInterface(context, hostedFileData)
    private var preferences: PreferencesInterface = PreferencesInterface(context, hostedFileData)


    private val groupsMap: MutableMap<String, ServiceInterface?> = mutableMapOf(
        "sqlite" to sqlite,
        "preferences" to preferences,
        "filesystem" to FilesystemInterface,
        "platform" to PlatformInterface
    )

    fun invokeGroup(group: String, service: String, body: String): ServiceResult {
        return groupsMap[group]?.invoke(service, body) ?: ServiceResult(null, false)
    }

    fun closeAll() {
        sqlite.close()
        preferences.close()
    }
}

