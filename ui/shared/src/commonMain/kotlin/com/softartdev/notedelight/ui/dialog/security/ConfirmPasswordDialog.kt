package com.softartdev.notedelight.ui.dialog.security

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.autofill.AutofillManager
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusDirection.Companion.Down
import androidx.compose.ui.platform.LocalAutofillManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.presentation.settings.security.confirm.ConfirmAction
import com.softartdev.notedelight.presentation.settings.security.confirm.ConfirmResult
import com.softartdev.notedelight.presentation.settings.security.confirm.ConfirmViewModel
import com.softartdev.notedelight.ui.PasswordField
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.cancel
import notedelight.ui.shared.generated.resources.confirm_password
import notedelight.ui.shared.generated.resources.confirm_password_dialog_subtitle
import notedelight.ui.shared.generated.resources.confirm_password_dialog_title
import notedelight.ui.shared.generated.resources.enter_password
import notedelight.ui.shared.generated.resources.yes
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ConfirmPasswordDialog(
    confirmViewModel: ConfirmViewModel,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val result: ConfirmResult by confirmViewModel.stateFlow.collectAsState()
    val autofillManager: AutofillManager? = LocalAutofillManager.current
    LaunchedEffect(key1 = confirmViewModel, key2 = autofillManager) {
        confirmViewModel.autofillManager = autofillManager
    }
    LaunchedEffect(key1 = confirmViewModel, key2 = result, key3 = result.snackBarMessageType) {
        result.snackBarMessageType?.let { msg: String ->
            snackbarHostState.showSnackbar(msg)
            confirmViewModel.disposeOneTimeEvents()
        }
    }
    ShowConfirmPasswordDialog(result, confirmViewModel::onAction)
}

@Composable
fun ShowConfirmPasswordDialog(
    result: ConfirmResult,
    onAction: (action: ConfirmAction) -> Unit = {}
) = AlertDialog(
    title = { Text(text = stringResource(Res.string.confirm_password_dialog_title)) },
    text = {
        val focusManager = LocalFocusManager.current
        Column {
            Text(text = stringResource(Res.string.confirm_password_dialog_subtitle))
            Spacer(modifier = Modifier.height(8.dp))
            if (result.loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            PasswordField(
                password = result.password,
                onPasswordChange = { onAction(ConfirmAction.OnEditPassword(it)) },
                label = result.passwordFieldLabel.resString,
                isError = result.isPasswordError,
                contentDescription = stringResource(Res.string.enter_password),
                passwordContentType = ContentType.NewPassword,
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(Down) }),
                imeAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(8.dp))
            PasswordField(
                password = result.repeatPassword,
                onPasswordChange = { onAction(ConfirmAction.OnEditRepeatPassword(it)) },
                label = result.repeatPasswordFieldLabel.resString,
                isError = result.isRepeatPasswordError,
                contentDescription = stringResource(Res.string.confirm_password),
                passwordContentType = ContentType.NewPassword,
                keyboardActions = KeyboardActions { onAction(ConfirmAction.OnConfirmClick) },
                imeAction = ImeAction.Done
            )
        }
    },
    confirmButton = { Button(onClick = { onAction(ConfirmAction.OnConfirmClick) }) { Text(stringResource(Res.string.yes)) } },
    dismissButton = { Button(onClick = { onAction(ConfirmAction.Cancel) }) { Text(stringResource(Res.string.cancel)) } },
    onDismissRequest = { onAction(ConfirmAction.Cancel) }
)

@Preview
@Composable
fun PreviewConfirmPasswordDialog() = PreviewDialog {
    ShowConfirmPasswordDialog(ConfirmResult())
}
