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
import com.softartdev.notedelight.presentation.settings.security.confirm.ConfirmResult
import com.softartdev.notedelight.presentation.settings.security.confirm.ConfirmViewModel
import com.softartdev.notedelight.ui.PasswordField
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.cancel
import notedelight.shared.generated.resources.confirm_password
import notedelight.shared.generated.resources.dialog_title_conform_password
import notedelight.shared.generated.resources.enter_password
import notedelight.shared.generated.resources.yes
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
            result.disposeOneTimeEvents()
        }
    }
    ShowConfirmPasswordDialog(result)
}

@Composable
fun ShowConfirmPasswordDialog(result: ConfirmResult) = AlertDialog(
    title = { Text(text = stringResource(Res.string.dialog_title_conform_password)) },
    text = {
        val focusManager = LocalFocusManager.current
        Column {
            if (result.loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            PasswordField(
                password = result.password,
                onPasswordChange = result.onEditPassword,
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
                onPasswordChange = result.onEditRepeatPassword,
                label = result.repeatPasswordFieldLabel.resString,
                isError = result.isRepeatPasswordError,
                contentDescription = stringResource(Res.string.confirm_password),
                passwordContentType = ContentType.NewPassword,
                keyboardActions = KeyboardActions { result.onConfirmClick.invoke() },
                imeAction = ImeAction.Done
            )
        }
    },
    confirmButton = { Button(onClick = result.onConfirmClick) { Text(stringResource(Res.string.yes)) } },
    dismissButton = { Button(onClick = result.onCancel) { Text(stringResource(Res.string.cancel)) } },
    onDismissRequest = result.onCancel
)

@Preview
@Composable
fun PreviewConfirmPasswordDialog() = PreviewDialog {
    ShowConfirmPasswordDialog(ConfirmResult())
}
