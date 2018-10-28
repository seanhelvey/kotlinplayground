package service

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import kotlinx.coroutines.experimental.withContext
import model.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.coroutines.experimental.CoroutineContext

object DatabaseFactory {

    fun init() {

        //todo: avoid running this each time
        Database.connect(hikari())
        transaction {
            create(Snippets)
            Snippets.insert {
                it[text] = "text one"
            }
            Snippets.insert {
                it[text] = "text two"
            }
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig().also {

            //todo: config for heroku
            it.jdbcUrl = "jdbc:postgresql://localhost:5432/practice_tracker";
            it.driverClassName = "org.postgresql.Driver"
            it.maximumPoolSize = 3
            it.isAutoCommit = false
            it.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            it.validate()
        }

        return HikariDataSource(config)
    }

    private val dispatcher: CoroutineContext

    init {
        dispatcher = newFixedThreadPoolContext(5, "database-pool")
    }

    suspend fun <T> dbQuery(
        block: () -> T): T =
        withContext(dispatcher) {
            transaction { block() }
        }

}