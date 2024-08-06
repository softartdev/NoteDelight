package com.softartdev.notedelight.ui.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import notedelight.shared_compose_ui.generated.resources.Res
import notedelight.shared_compose_ui.generated.resources.cancel
import notedelight.shared_compose_ui.generated.resources.error_title
import notedelight.shared_compose_ui.generated.resources.yes
import org.jetbrains.compose.resources.stringResource

@Composable
fun ErrorDialog(message: String?, dismissDialog: () -> Unit) = AlertDialog(
    title = { Text(text = stringResource(Res.string.error_title)) },
    text = { Text(message.orEmpty()) },
    confirmButton = { Button(onClick = dismissDialog) { Text(stringResource(Res.string.cancel)) } },
    onDismissRequest = dismissDialog,
)

@Composable
fun ShowDialog(
    title: String,
    text: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) = AlertDialog(
    title = { Text(title) },
    text = { Text(text.orEmpty()) },
    confirmButton = { Button(onClick = onConfirm) { Text(stringResource(Res.string.yes)) } },
    dismissButton = { Button(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) } },
    onDismissRequest = onDismiss,
)

@Preview
@Composable
fun PreviewErrorDialog() = PreviewDialog { ErrorDialog("preview err") {} }

@Preview
@Composable
fun PreviewDialog(dialogContent: @Composable () -> Unit) =
    Box(modifier = Modifier.fillMaxSize()) { dialogContent() }
