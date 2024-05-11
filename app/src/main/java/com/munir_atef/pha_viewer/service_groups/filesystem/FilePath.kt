package com.munir_atef.pha_viewer.service_groups.filesystem

import com.munir_atef.pha_viewer.shared.InUseFile
import com.munir_atef.pha_viewer.shared.SharedData


class FilePath(filePath: String?) {
    private val prefixForInternal = "::files"
    private var path: String? = null

    private val permissionState =
        InUseFile.hostedFileData?.agreedPermissions?.contains("filesystem") == true

    init {
        if (filePath?.startsWith(SharedData.externalStoragePath) == true && permissionState) path = filePath
        else if (filePath?.startsWith("$prefixForInternal/") == true)
            path = filePath.replaceFirst(prefixForInternal, "${InUseFile.hostedFileData?.rootPath}/files")

        println(path)
    }

    fun getPath(): String? = path
}

