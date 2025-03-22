package com.softartdev.notedelight.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.softartdev.notedelight.model.PlatformSQLiteState
import java.io.File
import java.io.FileNotFoundException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

object JvmCipherUtils {

    fun getDatabaseState(dbName: String): PlatformSQLiteState {
        var result = PlatformSQLiteState.DOES_NOT_EXIST
        val dbPath = File(dbName)
        if (dbPath.exists()) {
            result = PlatformSQLiteState.UNENCRYPTED
            var connection: Connection? = null
            try {
                val url = JdbcSqliteDriver.IN_MEMORY + dbName // jdbc:sqlite:$dbName
                connection = DriverManager.getConnection(url)
                val statement: Statement = connection.createStatement()
                val resultSet: ResultSet = statement.executeQuery("PRAGMA user_version;")
                val version: Long = resultSet.getLong(1)
                println("db version = $version")
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                result = PlatformSQLiteState.ENCRYPTED
            } finally {
                connection?.close()
            }
        }
        return result
    }

    fun decrypt(password: String, dbName: String) {
        val originalFile = File(dbName)
        if (originalFile.exists()) {
            val newFile = File.createTempFile("sqlcipherutils", "tmp", null)

            var url = JdbcSqliteDriver.IN_MEMORY + dbName // jdbc:sqlite:$dbName
            var connection = DriverManager.getConnection(url, null, password)
            val statement = connection.prepareStatement("ATTACH DATABASE ? AS plaintext KEY ''")
            statement.setString(1, newFile.absolutePath)
            statement.execute()
            connection.createStatement().executeQuery("SELECT sqlcipher_export('plaintext')")
            connection.createStatement().executeQuery("DETACH DATABASE plaintext")
            val resultSet: ResultSet = statement.executeQuery("PRAGMA user_version;")
            val version: Long = resultSet.getLong(1)
            statement.close()
            connection.close()

            url = JdbcSqliteDriver.IN_MEMORY + newFile.absolutePath
            connection = DriverManager.getConnection(url)
            connection.createStatement().executeQuery("PRAGMA user_version = $version")
            connection.close()

            originalFile.delete()
            newFile.renameTo(originalFile)
        } else {
            throw FileNotFoundException(originalFile.absolutePath + " not found")
        }
    }

    fun encrypt(password: String?, dbName: String) {
        val originalFile = File(dbName)
        if (originalFile.exists()) {
            val newFile = File.createTempFile("sqlcipherutils", "tmp", null)

            var url = JdbcSqliteDriver.IN_MEMORY + originalFile.absolutePath // jdbc:sqlite:${originalFile.absolutePath}
            var connection = DriverManager.getConnection(url)
            var statement: Statement = connection.createStatement()
            val resultSet: ResultSet = statement.executeQuery("PRAGMA user_version;")
            val version: Long = resultSet.getLong(1)
            statement.close()
            connection.close()

            url = JdbcSqliteDriver.IN_MEMORY + newFile.absolutePath
            connection = DriverManager.getConnection(url, null, password)
            statement = connection.prepareStatement("ATTACH DATABASE ? AS plaintext KEY ''")
            statement.setString(1, originalFile.absolutePath)
            statement.execute()
            connection.createStatement().executeQuery("SELECT sqlcipher_export('plaintext')")
            connection.createStatement().executeQuery("DETACH DATABASE plaintext")
            connection.createStatement().executeQuery("PRAGMA user_version = $version")
            statement.close()
            connection.close()

            originalFile.delete()
            newFile.renameTo(originalFile)
        } else {
            throw FileNotFoundException(originalFile.absolutePath + " not found")
        }
    }
}