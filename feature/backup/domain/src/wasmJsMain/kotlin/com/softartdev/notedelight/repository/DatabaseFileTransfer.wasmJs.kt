@file:OptIn(ExperimentalWasmJsInterop::class)

package com.softartdev.notedelight.repository

import kotlinx.coroutines.await
import kotlin.js.JsAny
import kotlin.js.Promise

actual object DatabaseFileTransfer {

    actual suspend fun copyDatabase(sourcePath: String, destinationPath: String) {
        val exportTarget: JsAny? = WasmDatabaseTransferRegistry.consumeExportTarget(destinationPath)
        if (exportTarget != null) {
            exportToHandle(sourcePath = sourcePath, exportTarget = exportTarget)
            return
        }
        val importSource: JsAny? = WasmDatabaseTransferRegistry.consumeImportSource(sourcePath)
        if (importSource != null) {
            importFromSource(importSource = importSource, destinationPath = destinationPath)
            return
        }
        throw UnsupportedOperationException("Database import/export on wasmJs requires a browser-picked source/target")
    }

    private suspend fun exportToHandle(sourcePath: String, exportTarget: JsAny) {
        val sourceHandle = wasmOpfsGetFileHandle(sourcePath, create = false).awaitJsAny()
        val sourceFile = wasmGetFileFromHandle(sourceHandle).awaitJsAny()
        val sourceBytes = wasmArrayBuffer(sourceFile).awaitJsAny()
        val writableStream = wasmCreateWritable(exportTarget).awaitJsAny()
        try {
            wasmWritableWrite(writableStream, sourceBytes).await<JsAny?>()
        } finally {
            wasmWritableClose(writableStream).await<JsAny?>()
        }
    }

    private suspend fun importFromSource(importSource: JsAny, destinationPath: String) {
        val sourceFile = wasmResolveImportFile(importSource).awaitJsAny()
        val sourceBytes = wasmArrayBuffer(sourceFile).awaitJsAny()
        val destinationHandle = wasmOpfsGetFileHandle(destinationPath, create = true).awaitJsAny()
        val writableStream = wasmCreateWritable(destinationHandle).awaitJsAny()
        try {
            wasmWritableWrite(writableStream, sourceBytes).await<JsAny?>()
        } finally {
            wasmWritableClose(writableStream).await<JsAny?>()
        }
    }

    private suspend fun Promise<JsAny?>.awaitJsAny(): JsAny = requireNotNull(await())
}
