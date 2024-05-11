package com.munir_atef.pha_viewer.shared

import android.os.Environment


object SharedData {
    const val EXTENSION: String = "pha"
    const val PRIVATE_AGENT: String = "Mozilla/5.0 (Linux; Android) PRIVATE_PHA"
    val externalStoragePath: String = Environment.getExternalStorageDirectory().path
    lateinit var rootForUnzipped: String
}

