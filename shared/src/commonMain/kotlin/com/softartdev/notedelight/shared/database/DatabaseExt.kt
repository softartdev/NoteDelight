package com.softartdev.notedelight.shared.database

import com.softartdev.notedelight.shared.db.NoteQueries
import app.cash.sqldelight.Transacter
import app.cash.sqldelight.TransactionWithReturn
import app.cash.sqldelight.TransactionWithoutReturn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

suspend fun Transacter.transactionWithContext(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    noEnclosing: Boolean = false,
    body: TransactionWithoutReturn.() -> Unit
) = withContext(coroutineContext) {
    this@transactionWithContext.transaction(noEnclosing, body)
}

suspend fun <R> Transacter.transactionResultWithContext(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    noEnclosing: Boolean = false,
    bodyWithReturn: TransactionWithReturn<R>.() -> R
): R = withContext(coroutineContext) {
    this@transactionResultWithContext.transactionWithResult(noEnclosing, bodyWithReturn)
}

val NoteQueries.lastInsertRowId: Long
    get() = transactionWithResult { lastInsertRowId().executeAsOne() }
