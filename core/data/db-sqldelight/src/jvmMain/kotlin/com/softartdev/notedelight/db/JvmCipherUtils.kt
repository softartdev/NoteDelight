package com.softartdev.notedelight.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.model.PlatformSQLiteState
import java.io.File
import java.io.FileNotFoundException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

object JvmCipherUtils {
    private val logger = Logger.withTag(this::class.simpleName.toString())
    private const val JDBC_PREFIX = "jdbc:sqlite:file:"
    private const val SQLCIPHER_CIPHER = "sqlcipher"
    private const val SQLCIPHER_LEGACY = "4"
    private const val SQLITE_JDBC_DRIVER = "org.sqlite.JDBC"

    init {
        // Ensure sqlite-jdbc-crypt driver is loaded and registered
        try {
            val driverClass = Class.forName(SQLITE_JDBC_DRIVER)
            val driver = driverClass.getDeclaredConstructor().newInstance() as java.sql.Driver
            // Register the driver explicitly to ensure it's used
            try {
                DriverManager.registerDriver(driver)
                logger.d { "SQLite JDBC driver (sqlite-jdbc-crypt) registered successfully" }
            } catch (e: java.sql.SQLException) {
                if (e.message?.contains("already registered") == true) {
                    logger.d { "SQLite JDBC driver already registered" }
                } else {
                    logger.w(e) { "Failed to register driver, but it may already be registered" }
                }
            }
        } catch (e: Exception) {
            logger.e(e) { "Failed to load SQLite JDBC driver: ${e.message}" }
        }
    }

    private fun buildEncryptedUrl(dbPath: String, password: String): String {
        val encodedPassword = URLEncoder.encode(password, StandardCharsets.UTF_8)
        return "$JDBC_PREFIX$dbPath?cipher=$SQLCIPHER_CIPHER&legacy=$SQLCIPHER_LEGACY&key=$encodedPassword"
    }
    
    private fun getConnection(url: String, isEncrypted: Boolean = false): Connection {
        return if (isEncrypted) {
            // For encrypted URLs, explicitly use sqlite-jdbc-crypt driver
            try {
                val driverClass = Class.forName(SQLITE_JDBC_DRIVER)
                val driver = driverClass.getDeclaredConstructor().newInstance() as java.sql.Driver
                val props = java.util.Properties()
                driver.connect(url, props) ?: throw java.sql.SQLException("Failed to connect to encrypted database")
            } catch (e: Exception) {
                logger.e(e) { "Failed to create encrypted connection, falling back to DriverManager" }
                DriverManager.getConnection(url)
            }
        } else {
            DriverManager.getConnection(url)
        }
    }

    fun getDatabaseState(dbName: String): PlatformSQLiteState {
        var result = PlatformSQLiteState.DOES_NOT_EXIST
        val dbPath = File(dbName)
        logger.d { "Checking database state for: $dbName (exists: ${dbPath.exists()})" }
        if (dbPath.exists()) {
            result = PlatformSQLiteState.UNENCRYPTED
            var connection: Connection? = null
            try {
                // Try to open as unencrypted database
                val url = JdbcSqliteDriver.IN_MEMORY + dbName // jdbc:sqlite:$dbName
                logger.d { "Attempting to open as unencrypted: $url" }
                connection = DriverManager.getConnection(url)
                val statement: Statement = connection.createStatement()
                val resultSet: ResultSet = statement.executeQuery("PRAGMA user_version;")
                val version: Long = resultSet.getLong(1)
                logger.d { "Database opened successfully as unencrypted, version: $version" }
                resultSet.close()
                statement.close()
            } catch (throwable: Throwable) {
                // If opening as unencrypted fails, assume it's encrypted
                logger.d(throwable) { "Failed to open as unencrypted, assuming encrypted: ${throwable.message}" }
                result = PlatformSQLiteState.ENCRYPTED
            } finally {
                connection?.close()
            }
        }
        logger.d { "Database state result: $result" }
        return result
    }

    fun decrypt(password: String, dbName: String) {
        logger.d { "Starting decrypt operation for: $dbName" }
        val originalFile = File(dbName)
        if (originalFile.exists()) {
            val newFile = File.createTempFile("sqlcipherutils", "tmp", null)
            logger.d { "Created temp file: ${newFile.absolutePath}" }

            try {
                // Open encrypted database using sqlite-jdbc-crypt URL format
                val encryptedUrl = buildEncryptedUrl(originalFile.absolutePath, password)
                logger.d { "Opening encrypted database: $encryptedUrl" }
                var connection = getConnection(encryptedUrl, isEncrypted = true)
                
                // Get version from encrypted database
                val versionResult: ResultSet = connection.createStatement().executeQuery("PRAGMA user_version;")
                val version: Long = versionResult.getLong(1)
                logger.d { "Retrieved version from encrypted DB: $version" }
                versionResult.close()

                // Attach unencrypted database and export
                logger.d { "Attaching unencrypted database for export" }
                val attachStatement = connection.prepareStatement("ATTACH DATABASE ? AS plaintext KEY ''")
                attachStatement.setString(1, newFile.absolutePath)
                attachStatement.execute()
                
                // Try sqlcipher_export first, fall back to manual copy if not available
                try {
                    connection.createStatement().execute("SELECT sqlcipher_export('plaintext')")
                    logger.d { "Used sqlcipher_export for decryption" }
                } catch (e: Exception) {
                    logger.w(e) { "sqlcipher_export not available, using manual copy: ${e.message}" }
                    // Manual copy: get schema and data from encrypted database (main database)
                    // Query the main (encrypted) database for table schemas
                    val tablesResult = connection.createStatement().executeQuery(
                        "SELECT name, sql FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'"
                    )
                    val tableSchemas = mutableListOf<Pair<String, String>>() // name to CREATE TABLE SQL
                    while (tablesResult.next()) {
                        val tableName = tablesResult.getString("name")
                        val createSql = tablesResult.getString("sql")
                        if (createSql != null) {
                            tableSchemas.add(tableName to createSql)
                        }
                    }
                    tablesResult.close()
                    
                    // Create tables in unencrypted (plaintext) database and copy data FROM main (encrypted) TO plaintext
                    for ((tableName, createSql) in tableSchemas) {
                        // Modify CREATE TABLE statement to create table in plaintext database
                        val plaintextCreateSql = createSql.replace("CREATE TABLE $tableName", "CREATE TABLE plaintext.$tableName")
                            .replace("CREATE TABLE IF NOT EXISTS $tableName", "CREATE TABLE IF NOT EXISTS plaintext.$tableName")
                        // Execute CREATE TABLE in plaintext (unencrypted) database
                        connection.createStatement().execute(plaintextCreateSql)
                        // Copy data FROM main (encrypted) TO plaintext (unencrypted)
                        connection.createStatement().execute("INSERT INTO plaintext.$tableName SELECT * FROM $tableName")
                        logger.d { "Copied table: $tableName from encrypted to unencrypted" }
                    }
                }
                
                connection.createStatement().execute("DETACH DATABASE plaintext")
                attachStatement.close()
                connection.close()
                logger.d { "Export completed" }

                // Open unencrypted database and set version
                val unencryptedUrl = JdbcSqliteDriver.IN_MEMORY + newFile.absolutePath
                logger.d { "Setting version on unencrypted database: $unencryptedUrl" }
                connection = DriverManager.getConnection(unencryptedUrl)
                connection.createStatement().execute("PRAGMA user_version = $version")
                connection.close()

                // Replace original with decrypted version
                logger.d { "Replacing original file with decrypted version" }
                originalFile.delete()
                newFile.renameTo(originalFile)
                logger.d { "Decrypt operation completed successfully" }
            } catch (e: Exception) {
                logger.e(e) { "Error during decrypt operation: ${e.message}" }
                newFile.delete()
                throw e
            }
        } else {
            throw FileNotFoundException(originalFile.absolutePath + " not found")
        }
    }

    fun encrypt(password: String?, dbName: String) {
        logger.d { "Starting encrypt operation for: $dbName" }
        val originalFile = File(dbName)
        if (originalFile.exists()) {
            if (password == null || password.isEmpty()) {
                throw IllegalArgumentException("Password cannot be null or empty for encryption")
            }
            
            val newFile = File.createTempFile("sqlcipherutils", "tmp", null)
            logger.d { "Created temp file: ${newFile.absolutePath}" }

            try {
                // Get version from unencrypted database
                val unencryptedUrl = JdbcSqliteDriver.IN_MEMORY + originalFile.absolutePath
                logger.d { "Reading version from unencrypted database: $unencryptedUrl" }
                var connection = DriverManager.getConnection(unencryptedUrl)
                val versionResult: ResultSet = connection.createStatement().executeQuery("PRAGMA user_version;")
                val version: Long = versionResult.getLong(1)
                logger.d { "Retrieved version from unencrypted DB: $version" }
                versionResult.close()
                connection.close()

                // Create encrypted database from the start using sqlite-jdbc-crypt URL format
                logger.d { "Creating encrypted database from the start" }
                val encryptedUrl = buildEncryptedUrl(newFile.absolutePath, password)
                connection = getConnection(encryptedUrl, isEncrypted = true)
                connection.autoCommit = true // SQLite commits each statement automatically
                
                // Set encryption key explicitly using PRAGMA key to ensure encryption is enabled
                try {
                    val escapedPassword = password.replace("'", "''")
                    connection.createStatement().execute("PRAGMA key = '$escapedPassword'")
                    logger.d { "Set encryption key via PRAGMA key" }
                } catch (e: Exception) {
                    logger.w(e) { "PRAGMA key failed (may already be set via URL): ${e.message}" }
                }
                
                // Verify encryption is working
                try {
                    val stmt = connection.createStatement()
                    val hasResultSet = stmt.execute("PRAGMA cipher_version;")
                    if (hasResultSet) {
                        val cipherVersion = stmt.resultSet
                        if (cipherVersion?.next() == true) {
                            logger.d { "Encryption is enabled, cipher version: ${cipherVersion.getString(1)}" }
                        }
                        cipherVersion?.close()
                    }
                    stmt.close()
                } catch (e: Exception) {
                    logger.w(e) { "Could not verify cipher version: ${e.message}" }
                }
                
                // Read data from original unencrypted database and write directly to encrypted database
                logger.d { "Reading data from original unencrypted database" }
                val unencryptedConn = DriverManager.getConnection(unencryptedUrl)
                try {
                    // Get all table schemas from unencrypted database
                    val tablesResult = unencryptedConn.createStatement().executeQuery(
                        "SELECT name, sql FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'"
                    )
                    val tableSchemas = mutableListOf<Pair<String, String>>() // name to CREATE TABLE SQL
                    while (tablesResult.next()) {
                        val tableName = tablesResult.getString("name")
                        val createSql = tablesResult.getString("sql")
                        if (createSql != null) {
                            tableSchemas.add(tableName to createSql)
                        }
                    }
                    tablesResult.close()
                    
                    // Create tables in encrypted database and copy data row by row
                    for ((tableName, createSql) in tableSchemas) {
                        logger.d { "Processing table: $tableName" }
                        // Execute CREATE TABLE in encrypted database
                        connection.createStatement().execute(createSql)
                        
                        // Get column info from unencrypted database
                        val columnsResult = unencryptedConn.createStatement().executeQuery(
                            "PRAGMA table_info($tableName)"
                        )
                        val columns = mutableListOf<String>()
                        val columnTypes = mutableMapOf<String, String>()
                        while (columnsResult.next()) {
                            val colName = columnsResult.getString("name")
                            val colType = columnsResult.getString("type")
                            columns.add(colName)
                            columnTypes[colName] = colType
                        }
                        columnsResult.close()
                        
                        // Read all rows from unencrypted database
                        val dataResult = unencryptedConn.createStatement().executeQuery(
                            "SELECT * FROM $tableName"
                        )
                        
                        // Prepare INSERT statement for encrypted database
                        val columnList = columns.joinToString(", ")
                        val placeholders = columns.joinToString(", ") { "?" }
                        val insertSql = "INSERT INTO $tableName ($columnList) VALUES ($placeholders)"
                        val insertStmt = connection.prepareStatement(insertSql)
                        
                        var rowCount = 0
                        while (dataResult.next()) {
                            // Set values for each column
                            columns.forEachIndexed { index, colName ->
                                val colType = columnTypes[colName]?.uppercase() ?: ""
                                when {
                                    colType.contains("INTEGER") -> insertStmt.setLong(index + 1, dataResult.getLong(colName))
                                    colType.contains("REAL") -> insertStmt.setDouble(index + 1, dataResult.getDouble(colName))
                                    colType.contains("BLOB") -> insertStmt.setBytes(index + 1, dataResult.getBytes(colName))
                                    else -> {
                                        val value = dataResult.getString(colName)
                                        if (value != null) {
                                            insertStmt.setString(index + 1, value)
                                        } else {
                                            insertStmt.setNull(index + 1, java.sql.Types.NULL)
                                        }
                                    }
                                }
                            }
                            insertStmt.addBatch()
                            rowCount++
                            
                            // Execute batch every 1000 rows
                            if (rowCount % 1000 == 0) {
                                insertStmt.executeBatch()
                                logger.d { "Copied $rowCount rows from $tableName" }
                            }
                        }
                        // Execute remaining batch
                        if (rowCount % 1000 != 0) {
                            insertStmt.executeBatch()
                        }
                        insertStmt.close()
                        dataResult.close()
                        logger.d { "Copied table: $tableName with $rowCount rows" }
                    }
                } finally {
                    unencryptedConn.close()
                }
                
                // Set version (autoCommit=true means each statement is committed automatically)
                connection.createStatement().execute("PRAGMA user_version = $version")
                connection.close()
                
                // Force a VACUUM to ensure all pages are encrypted
                try {
                    connection = getConnection(encryptedUrl, isEncrypted = true)
                    connection.autoCommit = true
                    val vacuumStmt = connection.createStatement()
                    vacuumStmt.execute("VACUUM")
                    vacuumStmt.close()
                    logger.d { "Executed VACUUM to ensure all pages are encrypted" }
                } catch (e: Exception) {
                    logger.w(e) { "VACUUM failed: ${e.message}" }
                }
                
                connection.close()
                logger.d { "Export completed" }
                
                // Close and reopen the connection to verify encryption is working
                // This ensures the database file is actually encrypted
                logger.d { "Verifying encryption by reopening the database" }
                val verifyConnection = getConnection(encryptedUrl, isEncrypted = true)
                try {
                    val verifyResult = verifyConnection.createStatement().executeQuery("PRAGMA user_version;")
                    if (verifyResult.next()) {
                        val verifiedVersion = verifyResult.getLong(1)
                        logger.d { "Verified encrypted database is readable with password, version: $verifiedVersion" }
                        if (verifiedVersion != version) {
                            logger.w { "Version mismatch: expected $version, got $verifiedVersion" }
                        }
                    }
                    verifyResult.close()
                } finally {
                    verifyConnection.close()
                }
                
                // Verify encryption by trying to open the database without a password
                // If encryption worked, this should fail
                // Use the standard sqlite-jdbc URL format (not sqlite-jdbc-crypt) to test
                try {
                    // Try to open with standard JDBC URL (should fail if encrypted)
                    val unencryptedTestUrl = "jdbc:sqlite:${newFile.absolutePath}"
                    val testConnection = DriverManager.getConnection(unencryptedTestUrl)
                    testConnection.createStatement().executeQuery("PRAGMA user_version;")
                    testConnection.close()
                    logger.e { "ERROR: Database can still be opened without password - encryption is NOT working!" }
                    throw RuntimeException("Database encryption failed - database can be opened without password")
                } catch (e: Exception) {
                    if (e is RuntimeException && e.message?.contains("encryption failed") == true) {
                        throw e
                    }
                    // Expected - database should not be readable without password
                    logger.d { "Good: Database cannot be opened without password - encryption is working: ${e.message}" }
                }

                // Replace original with encrypted version
                logger.d { "Replacing original file with encrypted version" }
                originalFile.delete()
                newFile.renameTo(originalFile)
                logger.d { "Encrypt operation completed successfully" }
            } catch (e: Exception) {
                logger.e(e) { "Error during encrypt operation: ${e.message}" }
                newFile.delete()
                throw e
            }
        } else {
            throw FileNotFoundException(originalFile.absolutePath + " not found")
        }
    }
}