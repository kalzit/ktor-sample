package io.api.sample

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.engine.apache.Apache
import io.ktor.client.response.readText
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.response.respondText
import io.ktor.routing.routing
import kotlinx.coroutines.async

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalLocationsAPI
@Location("/")
data class Index(val url: String)

@KtorExperimentalLocationsAPI
fun Application.module() {
    install(Locations)
    install(ContentNegotiation) {
        gson()
    }
    routing {
        get<Index> { p ->
            val start = System.currentTimeMillis();
            log.info("start")

            // async {...} 内が非同期処理
            val content = async {
                HttpClient(Apache).use { client ->
                    log.info("client request start: time ${System.currentTimeMillis() - start}ms")
                    val content = client.call(p.url).response.readText()
                    log.info("client request end: time ${System.currentTimeMillis() - start}ms")
                    content
                }
            }

            log.info("client processed: time ${System.currentTimeMillis() - start}ms")

            // await で非同期処理の完了を待ち、結果を取得できる
            call.respondText(content.await(), ContentType.Text.Plain)
        }
    }
}
