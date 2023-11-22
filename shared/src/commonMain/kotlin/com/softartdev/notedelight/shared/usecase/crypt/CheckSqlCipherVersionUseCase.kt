package com.softartdev.notedelight.shared.usecase.crypt

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import com.softartdev.notedelight.shared.db.SafeRepo

class CheckSqlCipherVersionUseCase(private val safeRepo: SafeRepo) : () -> String? {

    override fun invoke(): String? {
        val driver = safeRepo.buildDbIfNeed().driver
        val queryResult: QueryResult<String?> = driver.executeQuery(
            identifier = null,
            sql = "PRAGMA cipher_version;",
            parameters = 0,
            binders = null,
            mapper = this::map
        )
        return queryResult.value
    }

    private fun map(sqlCursor: SqlCursor): QueryResult<String?> = QueryResult.Value(
        value = if (sqlCursor.next().value) sqlCursor.getString(0) else null
    )
}
