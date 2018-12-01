package io.api.sample

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

data class Message(val content: String)

fun Application.module() {
    install(ContentNegotiation) {
        gson()
    }
    routing {
        get("/") {
            call.respond(Message("Hello World!"))
        }
    }
}
