package com.softartdev.notedelight.ui.settings.detail

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
actual fun rememberPlatformDatabaseFilePicker(): DatabaseFilePicker {
    val pathCallbackMutableState: MutableState<(String?) -> Unit> = remember { mutableStateOf({}) }
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream"),
        onResult = { uri: Uri? -> pathCallbackMutableState.value(uri?.toString()) }
    )
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? -> pathCallbackMutableState.value(uri?.toString()) }
    )
    return remember(exportLauncher, importLauncher) {
        AndroidDatabaseFilePicker(pathCallbackMutableState, exportLauncher, importLauncher)
    }
}

class AndroidDatabaseFilePicker(
    private val pathCallbackMutableState: MutableState<(String?) -> Unit>,
    private val exportLauncher: ManagedActivityResultLauncher<String, Uri?>,
    private val importLauncher: ManagedActivityResultLauncher<Array<String>, Uri?>
) : DatabaseFilePicker {

    /**
     * Launches when the user clicks the export button on the settings screen.
     */
    override fun launchExport(defaultFileName: String, onPicked: (String?) -> Unit) {
        pathCallbackMutableState.value = onPicked
        exportLauncher.launch(defaultFileName)
    }

    /**
     * Launches when the user clicks the import button on the settings screen.
     */
    override fun launchImport(onPicked: (String?) -> Unit) {
        pathCallbackMutableState.value = onPicked
        importLauncher.launch(arrayOf("*/*"))
    }
}
