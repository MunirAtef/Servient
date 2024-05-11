package com.munir_atef.pha_viewer.local_server.socket_server

import android.content.Context
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.munir_atef.pha_viewer.hosted_file.HostedFileData
import com.munir_atef.pha_viewer.local_server.LocalHost
import com.munir_atef.pha_viewer.service.Service
import com.munir_atef.pha_viewer.service.ServiceResult
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.*


class SocketServer(port: Int, context: Context, private val hostedFileData: HostedFileData): LocalHost() {
    private var serverSocket: ServerSocket = ServerSocket(port)
    private val service = Service(context, hostedFileData)

    override fun start() {
        Thread {
            try {
                while (!serverSocket.isClosed) {
                    val clientSocket: Socket = serverSocket.accept()
                    Thread { handleRequest(clientSocket) }.start()
                }
            } catch (e: SocketException) {
                println(e.message)
            }
        }.start()
    }

    override fun close() {
        serverSocket.close()
    }


    private fun handleRequest(socket: Socket) {
        val inputStream: BufferedReader = socket.getInputStream().bufferedReader()
        val outputStream = BufferedOutputStream(socket.getOutputStream())

        val request = Request(inputStream)

        val body: String? = request.body
        val url = request.url

        val success = when {
            request.isService -> serveRequestedService(url, body, outputStream)
            request.isFile -> serveRequestedFile(url, outputStream)
            else -> false
        }

        if (!success) outputStream.write("HTTP/1.1 404 Not Found\r\n\n".toByteArray())

        outputStream.flush()
        outputStream.close()
        inputStream.close()
        socket.close()
    }

    private fun serveRequestedService(url: String?, body: String?, outputStream: BufferedOutputStream): Boolean {
        println("Socket server serveRequestedService")
        val pathToService: List<String>? = url?.split("/")
        val group: String = pathToService?.get(2) ?: ""
        val serviceName: String = pathToService?.get(3) ?: ""

        if (!hostedFileData.agreedPermissions.contains(group) && group != "filesystem") return false

        if (pathToService != null) {
            val serviceResult: ServiceResult =
                service.invokeGroup(group, serviceName, body ?: "")


            if (serviceResult.passed) {
                outputStream.write("HTTP/1.1 200 OK\r\n".toByteArray())

                var responseBody: ByteArray? = null
                var responseType: String? = null

                when (val data: Any? = serviceResult.data) {
                    is String -> {
                        responseBody = data.toByteArray()
                        responseType = "text/plain"
                    }
                    is JsonObject -> {
                        responseBody = data.toString().toByteArray()
                        responseType = "application/octet-stream"
                    }
                    is JsonArray -> {
                        responseBody = data.toString().toByteArray()
                        responseType = "application/octet-stream"
                    }
                    is ByteArray -> {
                        responseBody = data
                        responseType = "application/octet-stream"
                    }
                    is File -> {
                        responseBody = data.readBytes()
                        responseType = "application/octet-stream"
                    }
                }

                if (responseBody != null) {
                    val response = "Content-Type: $responseType" +
                        "\r\nContent-Length: ${responseBody.size}\r\n\r\n"

                    outputStream.write(response.toByteArray() + responseBody)
                }
                return true
            }
        }
        return false
    }

    private fun serveRequestedFile(url: String?, outputStream: BufferedOutputStream): Boolean {
        println("Socket server serveRequestedFile")
        val filePath: String = hostedFileData.rootPath + url
        println(filePath)

        // Serve the requested file
        val file = File(filePath)
        if (file.isFile) {
            val mimeType = getMimeType(file)
            val fileLength = file.length().toInt()

            val response = "HTTP/1.1 200 OK\r\nContent-Type: $mimeType\r\nContent-Length: $fileLength\r\n\r\n"

            outputStream.write(response.toByteArray())

            val fileInputStream = FileInputStream(file)
            val buffer = ByteArray(1024)
            var bytesRead = fileInputStream.read(buffer)
            while (bytesRead != -1) {
                outputStream.write(buffer, 0, bytesRead)
                bytesRead = fileInputStream.read(buffer)
            }
            fileInputStream.close()
            return true
        }
        return false
    }

    private fun getMimeType(file: File): String {
        return when (file.extension.lowercase(Locale.getDefault())) {
            "html" -> "text/html"
            "css" -> "text/css"
            "js" -> "text/javascript"
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            "gif" -> "image/gif"
            else -> "application/octet-stream"
        }
    }
}




/**
 * SQLite
 * SharedPreferences
 * Filesystem
 * Platform
 */

