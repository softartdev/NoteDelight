@file:OptIn(ExperimentalWasmJsInterop::class)

package com.softartdev.notedelight.ui.settings.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.repository.WasmDatabaseTransferRegistry
import com.softartdev.notedelight.repository.wasmFileHandleName
import com.softartdev.notedelight.repository.wasmFirstArrayItem
import com.softartdev.notedelight.repository.wasmPickExportFileHandle
import com.softartdev.notedelight.repository.wasmShowOpenFilePicker
import com.softartdev.notedelight.repository.wasmSupportsExportPicker
import com.softartdev.notedelight.repository.wasmSupportsOpenFilePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlin.js.JsAny

@Composable
actual fun rememberPlatformDatabaseFilePicker(): DatabaseFilePicker {
    val coroutineScope = rememberCoroutineScope()
    return remember { WasmDatabaseFilePicker(coroutineScope = coroutineScope) }
}

class WasmDatabaseFilePicker(private val coroutineScope: CoroutineScope) : DatabaseFilePicker {

    /**
     * Launches when the user clicks the export button on the settings screen.
     */
    override fun launchExport(defaultFileName: String, onPicked: (String?) -> Unit) {
        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            val pickedPath: String? = runCatching {
                if (!wasmSupportsExportPicker()) return@runCatching null
                val targetHandle = wasmPickExportFileHandle(defaultFileName).await<JsAny?>()
                    ?: return@runCatching null
                WasmDatabaseTransferRegistry.registerExportTarget(
                    pathHint = defaultFileName,
                    fileHandle = targetHandle
                )
            }.onFailure { throwable ->
                Logger.withTag("WasmDatabaseFilePicker").d(throwable) { "Export picker canceled or unavailable" }
            }.getOrNull()
            onPicked(pickedPath)
        }
    }

    /**
     * Launches when the user clicks the import button on the settings screen.
     */
    override fun launchImport(onPicked: (String?) -> Unit) {
        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            val pickedPath: String? = runCatching {
                if (!wasmSupportsOpenFilePicker()) return@runCatching null
                val pickedHandles = wasmShowOpenFilePicker().await<JsAny?>()
                val sourceHandle: JsAny = wasmFirstArrayItem(pickedHandles)
                    ?: return@runCatching null
                WasmDatabaseTransferRegistry.registerImportSource(
                    pathHint = wasmFileHandleName(sourceHandle),
                    source = sourceHandle
                )
            }.onFailure { throwable ->
                Logger.withTag("WasmDatabaseFilePicker").d(throwable) { "Import picker canceled or unavailable" }
            }.getOrNull()
            onPicked(pickedPath)
        }
    }
}
