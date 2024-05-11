package com.munir_atef.pha_viewer.hosted_file

import com.munir_atef.pha_viewer.models.ManifestModel
import com.munir_atef.pha_viewer.models.SavedFileModel


class HostedFileData(val rootPath: String) {
    val manifest = ManifestModel("$rootPath/manifest.json")
    var metadata = SavedFileModel()
    var agreedPermissions: MutableSet<String> = mutableSetOf()
}

