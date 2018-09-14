import com.sun.xml.internal.ws.developer.SerializationFeature
import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.*
import io.ktor.jackson.jackson
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.lang.Compiler.enable

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        jackson {

        }
    }
    install(Routing) {
        get("/snippets") {
            call.respond(kotlin.collections.mapOf("OK" to true))
        }
    }
}

fun main(args: Array<String>) {
    val port = (System.getenv("PORT") ?: "8080").toInt()
    embeddedServer(Netty, port, watchPaths = listOf("BlogAppKt"), module = Application::module).start()
}