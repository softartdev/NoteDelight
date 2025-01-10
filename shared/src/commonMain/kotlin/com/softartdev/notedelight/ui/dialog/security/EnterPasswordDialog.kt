package com.softartdev.notedelight.ui.dialog.security

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.softartdev.notedelight.presentation.settings.security.enter.EnterResult
import com.softartdev.notedelight.presentation.settings.security.enter.EnterViewModel
import com.softartdev.notedelight.ui.PasswordField
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.cancel
import notedelight.shared.generated.resources.dialog_title_enter_password
import notedelight.shared.generated.resources.enter_password
import notedelight.shared.generated.resources.yes
import org.jetbrains.compose.resources.stringResource

@Composable
fun EnterPasswordDialog(
    enterViewModel: EnterViewModel,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val result: EnterResult by enterViewModel.stateFlow.collectAsState()

    LaunchedEffect(key1 = enterViewModel, key2 = result, key3 = result.snackBarMessageType) {
        result.snackBarMessageType?.let { msg: String ->
            snackbarHostState.showSnackbar(msg)
            result.disposeOneTimeEvents()
        }
    }
    ShowEnterPasswordDialog(result)
}

@Composable
fun ShowEnterPasswordDialog(result: EnterResult) = AlertDialog(
    title = { Text(text = stringResource(Res.string.dialog_title_enter_password)) },
    text = {
        Column {
            if (result.loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            PasswordField(
                password = result.password,
                onPasswordChange = result.onEditPassword,
                label = result.fieldLabel.resString,
                isError = result.isError,
                contentDescription = stringResource(Res.string.enter_password),
            )
        }
    },
    confirmButton = { Button(onClick = result.onEnterClick) { Text(stringResource(Res.string.yes)) } },
    dismissButton = { Button(onClick = result.onCancel) { Text(stringResource(Res.string.cancel)) } },
    onDismissRequest = result.onCancel
)

@Preview
@Composable
fun PreviewEnterPasswordDialog() = PreviewDialog { ShowEnterPasswordDialog(EnterResult()) }