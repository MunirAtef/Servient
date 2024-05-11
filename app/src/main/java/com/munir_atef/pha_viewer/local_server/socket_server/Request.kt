package com.munir_atef.pha_viewer.local_server.socket_server

import java.io.BufferedReader


class Request(inputStream: BufferedReader) {
    var url: String? = null
    private var contentLength: Int? = null
    var body: String? = null
    var isService: Boolean = false
    var isFile: Boolean = false

    companion object {
        private fun decodeUrl(encodedUrl: String): String {
            var decodedUrl = ""
            var i = -1

            while (++i < encodedUrl.length) {
                if (encodedUrl[i] == '%' && i + 2 < encodedUrl.length) {
                    val hex = encodedUrl.substring(i + 1, i + 3)
                    val decodedChar = Character.toChars(Integer.parseInt(hex, 16))[0]
                    decodedUrl += decodedChar
                    i += 2
                } else {
                    decodedUrl += encodedUrl[i]
                }
            }

            println("Decode {$encodedUrl} to {$decodedUrl}")
            return decodedUrl
        }
    }


    init {
        val headerLines: ArrayList<String> = arrayListOf()
        var line: String = inputStream.readLine()

        val parts = line.split(" ")
        val method: String = parts[0]
        println(parts[1])
        url = parts[1]

        if (method == "POST" && url?.startsWith("/service") == true) isService = true
        else if (method == "GET" && url?.startsWith("/src") == true) {
            isFile = true
            url = decodeUrl(url!!)
        }

        while (line != "") {
            headerLines.add(line)
            line = inputStream.readLine()
            if (line.startsWith("Content-Length:"))
                contentLength = line.split(" ")[1].toInt()
        }
        if (contentLength != null) {
            val array = CharArray(contentLength!!)
            inputStream.read(array)
            body = String(array)
        }
    }
}

