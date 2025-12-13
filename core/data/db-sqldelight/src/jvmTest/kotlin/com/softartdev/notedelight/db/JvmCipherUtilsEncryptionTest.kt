package com.softartdev.notedelight.db

import com.softartdev.notedelight.model.PlatformSQLiteState
import java.io.File
import java.sql.DriverManager
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class JvmCipherUtilsEncryptionTest {

    private val testDbPath = File(System.getProperty("java.io.tmpdir"), "test_notes_${System.currentTimeMillis()}.db").absolutePath

    @BeforeTest
    fun setUp() {
        // Clean up any existing test database
        File(testDbPath).delete()
    }

    @AfterTest
    fun tearDown() {
        // Clean up test database
        File(testDbPath).delete()
    }

    @Test
    fun testEncryptDecrypt() {
        val password = "testPassword123"
        
        // Create an unencrypted database file first
        // Use jdbc:sqlite:file: prefix to create a file-based database
        val url = "jdbc:sqlite:file:$testDbPath"
        val connection = DriverManager.getConnection(url)
        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS test (id TEXT PRIMARY KEY, data TEXT)")
        connection.createStatement().execute("INSERT INTO test (id, data) VALUES ('1', 'test data')")
        connection.createStatement().execute("PRAGMA user_version = 1")
        connection.close()
        
        // Verify it's unencrypted
        var state = JvmCipherUtils.getDatabaseState(testDbPath)
        assertEquals(PlatformSQLiteState.UNENCRYPTED, state, "Database should be unencrypted initially")
        
        // Test encryption
        JvmCipherUtils.encrypt(password, testDbPath)
        
        // Verify we can read from encrypted database with password
        // sqlite-jdbc-crypt uses URL parameters: cipher=sqlcipher&legacy=4&key=password
        val encodedPassword = java.net.URLEncoder.encode(password, java.nio.charset.StandardCharsets.UTF_8)
        val encryptedUrl = "jdbc:sqlite:file:$testDbPath?cipher=sqlcipher&legacy=4&key=$encodedPassword"
        val encryptedConnection = DriverManager.getConnection(encryptedUrl)
        val resultSet = encryptedConnection.createStatement().executeQuery("SELECT data FROM test WHERE id = '1'")
        assertEquals(true, resultSet.next(), "Should be able to read from encrypted database")
        assertEquals("test data", resultSet.getString("data"), "Data should match")
        resultSet.close()
        encryptedConnection.close()
        
        // Verify database cannot be opened without password (proves it's encrypted)
        try {
            val unencryptedUrl = "jdbc:sqlite:$testDbPath"
            val unencryptedConnection = DriverManager.getConnection(unencryptedUrl)
            unencryptedConnection.createStatement().executeQuery("SELECT data FROM test WHERE id = '1'")
            unencryptedConnection.close()
            // If we get here, encryption failed
            throw AssertionError("Database should not be readable without password")
        } catch (e: Exception) {
            // Expected - database should not be readable without password
            println("Correctly failed to read encrypted database without password: ${e.message}")
        }
        
        // Verify database state detection
        state = JvmCipherUtils.getDatabaseState(testDbPath)
        assertEquals(PlatformSQLiteState.ENCRYPTED, state, "Database should be encrypted")
        
        // Test decryption
        JvmCipherUtils.decrypt(password, testDbPath)
        
        // Verify database is unencrypted
        val stateAfterDecrypt = JvmCipherUtils.getDatabaseState(testDbPath)
        assertEquals(PlatformSQLiteState.UNENCRYPTED, stateAfterDecrypt, "Database should be unencrypted after decrypt")
        
        // Verify we can read from decrypted database
        val decryptedUrl = "jdbc:sqlite:file:$testDbPath"
        val decryptedConnection = DriverManager.getConnection(decryptedUrl)
        val decryptedResultSet = decryptedConnection.createStatement().executeQuery("SELECT data FROM test WHERE id = '1'")
        assertEquals(true, decryptedResultSet.next(), "Should be able to read from decrypted database")
        assertEquals("test data", decryptedResultSet.getString("data"), "Data should match after decrypt")
        decryptedResultSet.close()
        decryptedConnection.close()
    }

    @Test
    fun testGetDatabaseState() {
        // Test non-existent database
        val nonExistentPath = File(System.getProperty("java.io.tmpdir"), "non_existent_${System.currentTimeMillis()}.db").absolutePath
        val state = JvmCipherUtils.getDatabaseState(nonExistentPath)
        assertEquals(PlatformSQLiteState.DOES_NOT_EXIST, state, "Non-existent database should return DOES_NOT_EXIST")
    }

    @Test
    fun testCheckSqlCipherAvailable() {
        // Test if SQLCipher functions are available
        // sqlite-jdbc-crypt uses URL parameters: cipher=sqlcipher&legacy=4&key=password
        val password = "test"
        val dbPath = File(System.getProperty("java.io.tmpdir"), "test_cipher_check_${System.currentTimeMillis()}.db").absolutePath
        val encodedPassword = java.net.URLEncoder.encode(password, java.nio.charset.StandardCharsets.UTF_8)
        val url = "jdbc:sqlite:file:$dbPath?cipher=sqlcipher&legacy=4&key=$encodedPassword"
        
        try {
            val connection = DriverManager.getConnection(url)
            // Try to check cipher version
            val stmt = connection.createStatement()
            val hasResultSet = stmt.execute("PRAGMA cipher_version;")
            if (hasResultSet) {
                val resultSet = stmt.resultSet
                if (resultSet?.next() == true) {
                    val version = resultSet.getString(1)
                    println("SQLCipher version: $version")
                }
                resultSet?.close()
            }
            stmt.close()
            
            // Try to check if sqlcipher_export is available (it doesn't return a ResultSet, use execute)
            try {
                connection.createStatement().execute("SELECT sqlcipher_export('test');")
                println("sqlcipher_export is available")
            } catch (e: Exception) {
                println("sqlcipher_export error: ${e.message}")
                // sqlcipher_export may not be available in all SQLCipher versions
                // This is not a critical failure - encryption still works
            }
            connection.close()
            
            // Clean up
            File(dbPath).delete()
        } catch (e: Exception) {
            println("Error checking SQLCipher: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}
