package model

import org.jetbrains.exposed.sql.Table

object Snippets : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val text = varchar("name", 255)
}

data class Snippet(
    val text: String
)

data class PostSnippet(val snippet: PostSnippet.Text) {
    data class Text(val text: String)
}