import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import model.*
import org.jetbrains.exposed.sql.insert
import service.DatabaseFactory
import service.DatabaseFactory.dbQuery
import java.util.*

val snippets = Collections.synchronizedList(mutableListOf(
    Snippet("hello"),
    Snippet("world")
))

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)

    install(ContentNegotiation) {
        jackson {

        }
    }

    DatabaseFactory.init()

    install(Routing) {
        get("/") {
            call.respondText("API at /snippets")
        }
        get("/snippets") {
            call.respond(kotlin.collections.mapOf("OK" to true))
        }
        post("/snippets") {
            val post = call.receive<PostSnippet>()
            snippets += Snippet(post.snippet.text)
            call.respond(mapOf("OK" to true))

            //todo: refactor
            //see https://ryanharrison.co.uk/2018/04/14/kotlin-ktor-exposed-starter.html
            var key: Int? = 0
            dbQuery {
                key = Snippets.insert({
                    it[text] = post.snippet.text
                }) get Snippets.id
            }
        }
    }
    install(StatusPages){
        exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}

fun main(args: Array<String>) {
    val port = (System.getenv("PORT") ?: "8080").toInt()
    embeddedServer(Netty, port, watchPaths = listOf("BlogAppKt"), module = Application::module).start()
}