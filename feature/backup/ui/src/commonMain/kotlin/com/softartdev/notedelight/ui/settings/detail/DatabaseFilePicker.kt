package com.softartdev.notedelight.ui.settings.detail

import androidx.compose.runtime.Composable
import co.touchlab.kermit.Logger
import org.koin.compose.currentKoinScope

/**
 * Interface for picking database files.
 *
 * When the user clicks the export/import button on the settings screen,
 * the launchExport/launchImport function is called.
 */
interface DatabaseFilePicker {
    fun launchExport(defaultFileName: String, onPicked: (String?) -> Unit)
    fun launchImport(onPicked: (String?) -> Unit)
}

@Composable
fun rememberDatabaseFilePicker(): DatabaseFilePicker {
    val testDbFilePicker: DatabaseFilePicker? = currentKoinScope().getOrNull()
    Logger.withTag("rememberDatabaseFilePicker").d { "TestDatabaseFilePicker: $testDbFilePicker" }
    return testDbFilePicker ?: rememberPlatformDatabaseFilePicker()
}

@Composable
expect fun rememberPlatformDatabaseFilePicker(): DatabaseFilePicker
