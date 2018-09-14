import spark.kotlin.*

//Note: Runs on http://localhost:4567/hello

fun main(args: Array<String>) {
    val http: Http = ignite()

    http.get("/hello") {
        "Hello Spark Kotlin!"
    }
}