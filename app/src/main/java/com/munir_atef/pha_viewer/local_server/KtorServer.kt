package com.munir_atef.pha_viewer.local_server

import android.content.Context
import com.munir_atef.pha_viewer.hosted_file.HostedFileData
import com.munir_atef.pha_viewer.service.Service
import com.munir_atef.pha_viewer.shared.SharedData
import io.ktor.application.*
import io.ktor.content.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import java.io.File
import java.util.concurrent.TimeUnit


class KtorServer(port: Int, context: Context, private val hostedFileData: HostedFileData): LocalHost() {
    private val service: Service by lazy { Service(context, hostedFileData) }

    private val devMessage: String by lazy {
        context.assets.open("dev_message.html").bufferedReader().readText()
    }

    private val server: JettyApplicationEngine = embeddedServer(Jetty, port = port) {
        install(ContentNegotiation) { gson() }

        routing {
            get("/") { call.respond(TextContent(devMessage, ContentType.Text.Html)) }

            get("{path...}") { handleGet(call) }

            post("/service/{group}/{service}") { handlePost(call) }
        }
    }


    override fun start() { server.start(wait = false) }

    override fun close() {
        server.stop(0, 0, TimeUnit.MILLISECONDS)
        service.closeAll()
    }

    private suspend fun handleGet(call: ApplicationCall) {
        println("Ktor server handle get")
        if (call.request.headers["User-Agent"] != SharedData.PRIVATE_AGENT) return

        val path = call.parameters.getAll("path")?.joinToString("/") ?: return
        if (path.indexOf("::files/") > -1) {
            val relativePath = path.split("::files").last()
            println("${hostedFileData.rootPath}$relativePath")
            call.respondFile(File("${hostedFileData.rootPath}/files$relativePath"))
        } else if (path.startsWith("src")) {
            val file = File("${hostedFileData.rootPath}/$path")
            if (file.exists()) call.respondFile(file)
        }
    }

    private suspend fun handlePost(call: ApplicationCall) {
        println("Ktor server handle post")
        if (call.request.headers["User-Agent"] != SharedData.PRIVATE_AGENT) return

        val group: String = call.parameters["group"] ?: ""
        val serviceName: String = call.parameters["service"] ?: ""

        if (hostedFileData.agreedPermissions.contains(group) || group == "filesystem") {
            val body: String = call.receive()
            print("$group/$serviceName")
            val result = service.invokeGroup(group, serviceName, body)
            if (result.passed) {
                when (val data = result.data) {
                    is ByteArray -> call.respondBytes(data)
                    is File -> call.respondFile(data)
                    is String -> call.respondText(data)
                    else -> call.respond(data ?: "")
                }
            }
        }
    }
}

