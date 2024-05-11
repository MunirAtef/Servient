package com.munir_atef.pha_viewer.service


object ProvidedServices {
    val services: MutableMap<String, ServiceDescription> = mutableMapOf(
        "sqlite" to ServiceDescription(
            "sqlite",
            "Local Database",
            "Read and write data from local SQLite file",
            true
        ),

        "preferences" to ServiceDescription(
            "preferences",
            "Shared Preferences",
            "Read and write data from local Shared Preferences",
            true
        ),

        "filesystem" to ServiceDescription(
            "filesystem",
            "Storage",
            "Access files, photos, and media in the device",
            false
        ),

        "platform" to ServiceDescription(
            "platform",
            "Platform Information",
            "Access Platform information e.g. OS name, OS version, device model.. etc.",
            true
        ),
    )
}


