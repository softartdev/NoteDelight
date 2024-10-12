package com.softartdev.notedelight.ui.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import notedelight.shared_compose_ui.generated.resources.Res
import notedelight.shared_compose_ui.generated.resources.cancel
import notedelight.shared_compose_ui.generated.resources.error_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ErrorDialog(message: String?, dismissDialog: () -> Unit) = AlertDialog(
    title = { Text(text = stringResource(Res.string.error_title)) },
    text = { Text(message.orEmpty()) },
    confirmButton = { Button(onClick = dismissDialog) { Text(stringResource(Res.string.cancel)) } },
    onDismissRequest = dismissDialog,
)

@Preview
@Composable
fun PreviewErrorDialog() = PreviewDialog { ErrorDialog("preview err") {} }
