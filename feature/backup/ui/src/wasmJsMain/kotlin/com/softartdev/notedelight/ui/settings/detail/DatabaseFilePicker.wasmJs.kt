package com.softartdev.notedelight.ui.settings.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberPlatformDatabaseFilePicker(): DatabaseFilePicker = remember {
    return@remember WasmDatabaseFilePicker()
}

class WasmDatabaseFilePicker : DatabaseFilePicker {

    /**
     * Launches when the user clicks the export button on the settings screen.
     */
    override fun launchExport(defaultFileName: String, onPicked: (String?) -> Unit) = onPicked(null)

    /**
     * Launches when the user clicks the import button on the settings screen.
     */
    override fun launchImport(onPicked: (String?) -> Unit) = onPicked(null)
}
