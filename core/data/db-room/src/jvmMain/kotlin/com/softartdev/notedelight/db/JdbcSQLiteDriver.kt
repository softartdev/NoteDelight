package com.softartdev.notedelight.db

import androidx.sqlite.SQLITE_DATA_BLOB
import androidx.sqlite.SQLITE_DATA_FLOAT
import androidx.sqlite.SQLITE_DATA_INTEGER
import androidx.sqlite.SQLITE_DATA_NULL
import androidx.sqlite.SQLITE_DATA_TEXT
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.SQLiteStatement
import java.math.BigDecimal
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.Properties

internal class JdbcSQLiteDriver(private val properties: Properties) : SQLiteDriver {
    override fun open(fileName: String): SQLiteConnection = JdbcSQLiteConnection(
        DriverManager.getConnection(buildJdbcUrl(fileName, properties)),
    )

    companion object {
        init {
            Class.forName("org.sqlite.JDBC")
        }

        private fun buildJdbcUrl(fileName: String, properties: Properties): String {
            val password = properties.getProperty("password")
            if (password.isNullOrEmpty()) return "jdbc:sqlite:$fileName"

            val encodedPassword = URLEncoder.encode(password, StandardCharsets.UTF_8)
            return "jdbc:sqlite:file:$fileName" +
                "?cipher=sqlcipher&legacy=4&key=$encodedPassword"
        }
    }
}

private class JdbcSQLiteConnection(private val delegate: Connection) : SQLiteConnection {
    override fun inTransaction(): Boolean = !delegate.autoCommit

    override fun prepare(sql: String): SQLiteStatement {
        val normalizedSql = sql.trim().uppercase()
        return when {
            normalizedSql.startsWith("BEGIN") -> TransactionStatement {
                if (delegate.autoCommit) delegate.autoCommit = false
            }
            normalizedSql == "COMMIT" || normalizedSql == "END" ||
                normalizedSql.startsWith("END TRANSACTION") -> TransactionStatement {
                    if (!delegate.autoCommit) {
                        delegate.commit()
                        delegate.autoCommit = true
                    }
                }
            normalizedSql == "ROLLBACK" || normalizedSql.startsWith("ROLLBACK TRANSACTION") ->
                TransactionStatement {
                    if (!delegate.autoCommit) {
                        delegate.rollback()
                        delegate.autoCommit = true
                    }
                }
            else -> JdbcSQLiteStatement(delegate.prepareStatement(sql))
        }
    }

    override fun close() = delegate.close()
}

private class JdbcSQLiteStatement(
    private val delegate: PreparedStatement,
) : SQLiteStatement {
    private var resultSet: ResultSet? = null
    private var executed = false

    override fun bindBlob(index: Int, value: ByteArray) = delegate.setBytes(index, value)
    override fun bindDouble(index: Int, value: Double) = delegate.setDouble(index, value)
    override fun bindLong(index: Int, value: Long) = delegate.setLong(index, value)
    override fun bindText(index: Int, value: String) = delegate.setString(index, value)
    override fun bindNull(index: Int) = delegate.setObject(index, null)

    override fun getBlob(index: Int): ByteArray = currentResultSet().getBytes(index + 1)
    override fun getDouble(index: Int): Double = currentResultSet().getDouble(index + 1)
    override fun getLong(index: Int): Long = currentResultSet().getLong(index + 1)
    override fun getText(index: Int): String = currentResultSet().getString(index + 1)
    override fun isNull(index: Int): Boolean = currentResultSet().getObject(index + 1) == null

    override fun getColumnCount(): Int = metadata().columnCount
    override fun getColumnName(index: Int): String = metadata().getColumnName(index + 1)
    override fun getColumnType(index: Int): Int = when (currentResultSet().getObject(index + 1)) {
        null -> SQLITE_DATA_NULL
        is ByteArray -> SQLITE_DATA_BLOB
        is Float, is Double, is BigDecimal -> SQLITE_DATA_FLOAT
        is Number, is Boolean -> SQLITE_DATA_INTEGER
        else -> SQLITE_DATA_TEXT
    }

    override fun step(): Boolean {
        if (!executed) {
            executed = true
            if (delegate.execute()) {
                resultSet = delegate.resultSet
            } else {
                return false
            }
        }
        return resultSet?.next() == true
    }

    override fun reset() {
        resultSet?.close()
        resultSet = null
        executed = false
    }

    override fun clearBindings() = delegate.clearParameters()

    override fun close() {
        resultSet?.close()
        delegate.close()
    }

    private fun currentResultSet(): ResultSet =
        requireNotNull(resultSet) { "Statement has no current result row" }

    private fun metadata(): java.sql.ResultSetMetaData {
        if (!executed) {
            executed = true
            if (delegate.execute()) {
                resultSet = delegate.resultSet
            }
        }
        return requireNotNull(resultSet?.metaData ?: delegate.metaData) {
            "Statement has no result metadata"
        }
    }
}

private class TransactionStatement(private val action: () -> Unit) : SQLiteStatement {
    private var executed = false

    override fun step(): Boolean {
        if (!executed) {
            action()
            executed = true
        }
        return false
    }

    override fun reset() {
        executed = false
    }

    override fun clearBindings() = Unit
    override fun close() = Unit

    override fun bindBlob(index: Int, value: ByteArray) = unsupported()
    override fun bindDouble(index: Int, value: Double) = unsupported()
    override fun bindLong(index: Int, value: Long) = unsupported()
    override fun bindText(index: Int, value: String) = unsupported()
    override fun bindNull(index: Int) = unsupported()
    override fun getBlob(index: Int): ByteArray = unsupported()
    override fun getDouble(index: Int): Double = unsupported()
    override fun getLong(index: Int): Long = unsupported()
    override fun getText(index: Int): String = unsupported()
    override fun isNull(index: Int): Boolean = unsupported()
    override fun getColumnCount(): Int = 0
    override fun getColumnName(index: Int): String = unsupported()
    override fun getColumnType(index: Int): Int = unsupported()

    private fun unsupported(): Nothing = error("Transaction statement has no values")
}
