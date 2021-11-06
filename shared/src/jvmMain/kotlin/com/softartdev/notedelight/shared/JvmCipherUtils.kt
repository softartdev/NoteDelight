package com.softartdev.notedelight.shared

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import java.io.File
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
}