package io.api.sample

import com.google.gson.Gson
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.engine.apache.Apache
import io.ktor.client.response.readText
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.routing
import kotlinx.coroutines.async

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalLocationsAPI
@Location("/{page}/{lang?}")
data class Index(val page: String, val lang: String = "")

data class WikiPedia(val parse: Parse?)
data class Parse(val title: String, val langlinks: List<Langlink>)
data class Langlink(val lang: String, val url: String)

@KtorExperimentalLocationsAPI
fun Application.module() {
    install(Locations)
    install(ContentNegotiation) {
        gson()
    }
    routing {
        get<Index> { p ->
            val uri = "https://ja.wikipedia.org/w/api.php?action=parse&format=json&page=${p.page}&prop=langlinks"
            val content = async {
                HttpClient(Apache).use { client ->
                    client.call(uri).response.readText()
                }
            }
            val parse = Gson().fromJson(content.await(), WikiPedia::class.java).parse ?: Parse("", emptyList())
            val langlinks = when (p.lang.isEmpty()) {
                true  -> parse.langlinks
                false -> parse.langlinks.filter { it.lang == p.lang }
            }
            call.respond(parse.copy(langlinks = langlinks))
        }
    }
}

