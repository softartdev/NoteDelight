package com.softartdev.notedelight.ui.files

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
actual fun PermissionDialog(dismissCallback: () -> Unit) {
    AlertDialog(
        onDismissRequest = dismissCallback,
        title = { Text("Permissions") },
        text = { Text("Permission handling not implemented for JVM") },
        confirmButton = {
            Button(onClick = dismissCallback) {
                Text("OK")
            }
        }
    )
}
