package io.api.sample

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

data class Message(val content: String)

@KtorExperimentalLocationsAPI
@Location("/{name}")
data class Index(val name: String)

@KtorExperimentalLocationsAPI
fun Application.module() {
    install(Locations)
    install(ContentNegotiation) {
        gson()
    }
    routing {
        get<Index>{ p ->
            call.respond(Message("Hello, ${p.name}"))
        }
    }
}
