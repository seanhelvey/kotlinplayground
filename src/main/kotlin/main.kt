import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.util.*

data class Snippet(val text: String)

data class PostSnippet(val snippet: PostSnippet.Text) {
    data class Text(val text: String)
}

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
        }
    }
}

fun main(args: Array<String>) {
    val port = (System.getenv("PORT") ?: "8080").toInt()
    embeddedServer(Netty, port, watchPaths = listOf("BlogAppKt"), module = Application::module).start()
}