package com.softartdev.notedelight.ui.settings.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
actual fun rememberPlatformDatabaseFilePicker(): DatabaseFilePicker = remember {
    return@remember JvmDatabaseFilePicker()
}

class JvmDatabaseFilePicker : DatabaseFilePicker {

    /**
     * Launches when the user clicks the export button on the settings screen.
     */
    override fun launchExport(defaultFileName: String, onPicked: (String?) -> Unit) {
        val dialog = FileDialog(null as Frame?, "Export Database", FileDialog.SAVE)
        dialog.file = defaultFileName
        dialog.isVisible = true
        val fileName: String? = dialog.file
        val directory: String? = dialog.directory
        if (fileName.isNullOrEmpty() || directory.isNullOrEmpty()) {
            onPicked(null)
            return
        }
        onPicked(File(directory, fileName).absolutePath)
    }

    /**
     * Launches when the user clicks the import button on the settings screen.
     */
    override fun launchImport(onPicked: (String?) -> Unit) {
        val dialog = FileDialog(null as Frame?, "Import Database", FileDialog.LOAD)
        dialog.isVisible = true
        val fileName: String? = dialog.file
        val directory: String? = dialog.directory
        if (fileName.isNullOrEmpty() || directory.isNullOrEmpty()) {
            onPicked(null)
            return
        }
        onPicked(File(directory, fileName).absolutePath)
    }
}
