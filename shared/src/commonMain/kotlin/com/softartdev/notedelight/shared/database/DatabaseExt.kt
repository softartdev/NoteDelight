package com.softartdev.notedelight.shared.database

import com.squareup.sqldelight.Transacter
import com.squareup.sqldelight.TransactionWithReturn
import com.squareup.sqldelight.TransactionWithoutReturn
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