package com.munir_atef.pha_viewer.local_server


import android.content.Context
import com.munir_atef.pha_viewer.hosted_file.HostedFileData
import com.munir_atef.pha_viewer.local_server.socket_server.SocketServer
import com.munir_atef.pha_viewer.shared.InUseFile

object ServerObject {
    private var server: LocalHost? = null

    fun initialServer(context: Context, hostedFileData: HostedFileData) {
        val useKtor = InUseFile.useKtorServer.value

        if (server == null) {
            server = if (useKtor) KtorServer(8080, context, hostedFileData).apply { start() }
            else SocketServer(8080, context, hostedFileData).apply { start() }
        }
    }

    fun endServer() {
        server?.close()
        server = null
    }
}
