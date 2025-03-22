package com.softartdev.notedelight.ui.dialog.security

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.presentation.settings.security.change.ChangeResult
import com.softartdev.notedelight.presentation.settings.security.change.ChangeViewModel
import com.softartdev.notedelight.ui.PasswordField
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.cancel
import notedelight.shared.generated.resources.dialog_title_change_password
import notedelight.shared.generated.resources.enter_new_password
import notedelight.shared.generated.resources.enter_old_password
import notedelight.shared.generated.resources.repeat_new_password
import notedelight.shared.generated.resources.yes
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChangePasswordDialog(
    changeViewModel: ChangeViewModel,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val result: ChangeResult by changeViewModel.stateFlow.collectAsState()

    LaunchedEffect(key1 = changeViewModel, key2 = result, key3 = result.snackBarMessageType) {
        result.snackBarMessageType?.let { msg: String ->
            snackbarHostState.showSnackbar(msg)
            result.disposeOneTimeEvents()
        }
    }
    ShowChangePasswordDialog(result)
}

@Composable
fun ShowChangePasswordDialog(result: ChangeResult) = AlertDialog(
    title = { Text(text = stringResource(Res.string.dialog_title_change_password)) },
    text = {
        Column {
            if (result.loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            PasswordField(
                password = result.oldPassword,
                onPasswordChange = result.onEditOldPassword,
                label = result.oldPasswordFieldLabel.resString,
                isError = result.isOldPasswordError,
                contentDescription = stringResource(Res.string.enter_old_password)
            )
            Spacer(modifier = Modifier.height(8.dp))
            PasswordField(
                password = result.newPassword,
                onPasswordChange = result.onEditNewPassword,
                label = result.newPasswordFieldLabel.resString,
                isError = result.isNewPasswordError,
                contentDescription = stringResource(Res.string.enter_new_password)
            )
            Spacer(modifier = Modifier.height(8.dp))
            PasswordField(
                password = result.repeatNewPassword,
                onPasswordChange = result.onEditRepeatPassword,
                label = result.repeatPasswordFieldLabel.resString,
                isError = result.isRepeatPasswordError,
                contentDescription = stringResource(Res.string.repeat_new_password)
            )
        }
    },
    confirmButton = { Button(onClick = result.onChangeClick) { Text(stringResource(Res.string.yes)) } },
    dismissButton = { Button(onClick = result.onCancel) { Text(stringResource(Res.string.cancel)) } },
    onDismissRequest = result.onCancel
)

@Preview
@Composable
fun PreviewChangePasswordDialog() = PreviewDialog {
    ShowChangePasswordDialog(ChangeResult())
}
