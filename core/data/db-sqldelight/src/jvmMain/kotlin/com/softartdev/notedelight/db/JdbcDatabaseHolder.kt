package com.softartdev.notedelight.db

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.async.coroutines.awaitMigrate
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import co.touchlab.kermit.Logger
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.sql.SQLException
import java.util.Properties

class JdbcDatabaseHolder(props: Properties = Properties()) : SqlDelightDbHolder {
    private val logger = Logger.withTag(this@JdbcDatabaseHolder::class.simpleName.toString())
    private val dbPath = FilePathResolver().invoke()

    val jdbcUrl: String = buildJdbcUrl(dbPath, props)
    override val driver: SqlDriver = createDriver(jdbcUrl, props)
    override val noteDb: NoteDb = createQueryWrapper(driver)
    override val noteQueries = noteDb.noteQueries

    private var currentVersion: Int
        get() {
            val queryResult = driver.execute(null, "PRAGMA user_version;", 0, null)
            val ver: Long = queryResult.value
            return ver.toInt()
        }
        set(value) {
            driver.execute(null, "PRAGMA user_version = $value;", 0, null)
        }

    override suspend fun createSchema() {
        if (currentVersion == 0) {
            try {
                NoteDb.Schema.awaitCreate(driver)
            } catch (sqlException: SQLException) {
                logger.e { sqlException.localizedMessage }
            } catch (t: Throwable) {
                logger.e(t) { "Error creating database schema" }
            }
            currentVersion = 1
        } else if (NoteDb.Schema.version > currentVersion) {
            NoteDb.Schema.awaitMigrate(driver, currentVersion.toLong(), NoteDb.Schema.version)
        }
    }

    override fun close() = driver.close()

    companion object {
        private const val JDBC_PREFIX_ENCRYPTED = "jdbc:sqlite:file:"
        private const val SQLCIPHER_CIPHER = "sqlcipher"
        private const val SQLCIPHER_LEGACY = "4"

        init {
            // Ensure sqlite-jdbc driver is loaded
            try {
                Class.forName("org.sqlite.JDBC")
                Logger.withTag("JdbcDatabaseHolder").d { "SQLite JDBC driver loaded successfully" }
            } catch (e: ClassNotFoundException) {
                Logger.withTag("JdbcDatabaseHolder").e(e) { "Failed to load SQLite JDBC driver" }
            }
        }

        /**
         * Creates a JDBC driver with optional SQLCipher encryption.
         * 
         * Uses Willena's sqlite-jdbc-crypt library which provides SQLCipher support
         * through SQLite3 Multiple Ciphers.
         * 
         * The encryption is configured via the JDBC URL with parameters:
         * - `cipher=sqlcipher` - Use SQLCipher encryption
         * - `legacy=4` - Use SQLCipher version 4 format
         * - `key=password` - The encryption passphrase
         * 
         * @param url The database file path (absolute path)
         * @param password Optional encryption password. If provided, the database will be encrypted.
         * @return A SqlDriver instance
         */
        fun buildJdbcUrl(dbPath: String, props: Properties): String {
            val logger = Logger.withTag("JdbcDatabaseHolder")
            val password: String? = props.getProperty("password")
            return if (password.isNullOrEmpty()) {
                val unencryptedUrl = JdbcSqliteDriver.IN_MEMORY + dbPath
                logger.d { "Creating unencrypted driver with URL: $unencryptedUrl" }
                unencryptedUrl
            } else {
                val encodedPassword = URLEncoder.encode(password, StandardCharsets.UTF_8)
                val encryptedUrl = "$JDBC_PREFIX_ENCRYPTED$dbPath?cipher=$SQLCIPHER_CIPHER&legacy=$SQLCIPHER_LEGACY&key=$encodedPassword"
                logger.d { "Creating encrypted driver with URL: $encryptedUrl (password length: ${password.length})" }
                encryptedUrl
            }
        }

        private fun createDriver(jdbcUrl: String, props: Properties): SqlDriver {
            val driverProps = Properties()
            for (key in props.stringPropertyNames()) {
                if (key != "password") {
                    driverProps.setProperty(key, props.getProperty(key))
                }
            }
            return JdbcSqliteDriver(jdbcUrl, driverProps)
        }
    }
}
