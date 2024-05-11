package com.munir_atef.pha_viewer.shared

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.munir_atef.pha_viewer.hosted_file.HostedFileData

object InUseFile {
    val useKtorServer: MutableState<Boolean> = mutableStateOf(false)
    var hostedFileData: HostedFileData? = null
    fun clear() { hostedFileData = null }
}
