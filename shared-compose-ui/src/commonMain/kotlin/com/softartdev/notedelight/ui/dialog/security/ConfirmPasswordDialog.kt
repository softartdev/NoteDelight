package com.softartdev.notedelight.ui.dialog.security

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.softartdev.notedelight.shared.presentation.settings.security.confirm.ConfirmResult
import com.softartdev.notedelight.shared.presentation.settings.security.confirm.ConfirmViewModel
import com.softartdev.notedelight.ui.PasswordField
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import kotlinx.coroutines.launch
import notedelight.shared_compose_ui.generated.resources.Res
import notedelight.shared_compose_ui.generated.resources.cancel
import notedelight.shared_compose_ui.generated.resources.confirm_password
import notedelight.shared_compose_ui.generated.resources.dialog_title_conform_password
import notedelight.shared_compose_ui.generated.resources.empty_password
import notedelight.shared_compose_ui.generated.resources.enter_password
import notedelight.shared_compose_ui.generated.resources.error_title
import notedelight.shared_compose_ui.generated.resources.passwords_do_not_match
import notedelight.shared_compose_ui.generated.resources.yes
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConfirmPasswordDialog(dismissDialog: () -> Unit, confirmViewModel: ConfirmViewModel) {
    val confirmResultState: State<ConfirmResult> = confirmViewModel.resultStateFlow.collectAsState()
    DisposableEffect(confirmViewModel) {
        onDispose(confirmViewModel::onCleared)
    }
    var labelResource by remember { mutableStateOf(Res.string.enter_password) }
    var error by remember { mutableStateOf(false) }
    var repeatLabelResource by remember { mutableStateOf(Res.string.confirm_password) }
    var repeatError by remember { mutableStateOf(false) }
    val passwordState: MutableState<String> = remember { mutableStateOf("") }
    val repeatPasswordState: MutableState<String> = remember { mutableStateOf("") }
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    when (val confirmResult: ConfirmResult = confirmResultState.value) {
        is ConfirmResult.InitState, is ConfirmResult.Loading -> Unit
        is ConfirmResult.Success -> dismissDialog()
        is ConfirmResult.EmptyPasswordError -> {
            labelResource = Res.string.empty_password
            error = true
        }
        is ConfirmResult.PasswordsNoMatchError -> {
            repeatLabelResource = Res.string.passwords_do_not_match
            repeatError = true
        }
        is ConfirmResult.Error -> coroutineScope.launch {
            snackbarHostState.showSnackbar(confirmResult.message ?: getString(Res.string.error_title))
        }
    }
    ShowConfirmPasswordDialog(
        showLoaing = confirmResultState.value is ConfirmResult.Loading,
        passwordState = passwordState,
        repeatPasswordState = repeatPasswordState,
        labelResource = labelResource,
        repeatLabelResource = repeatLabelResource,
        isError = error,
        isRepeatError = repeatError,
        snackbarHostState = snackbarHostState,
        dismissDialog = dismissDialog
    ) { confirmViewModel.conformCheck(password = passwordState.value, repeatPassword = repeatPasswordState.value) }
}

@Composable
fun ShowConfirmPasswordDialog(
    showLoaing: Boolean = true,
    passwordState: MutableState<String> = mutableStateOf("password"),
    repeatPasswordState: MutableState<String> = mutableStateOf("repeat password"),
    labelResource: StringResource = Res.string.enter_password,
    repeatLabelResource: StringResource = Res.string.confirm_password,
    isError: Boolean = false,
    isRepeatError: Boolean = true,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    dismissDialog: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
) = AlertDialog(
    title = { Text(text = stringResource(Res.string.dialog_title_conform_password)) },
    text = {
        Column {
            if (showLoaing) LinearProgressIndicator()
            PasswordField(
                passwordState = passwordState,
                label = stringResource(labelResource),
                isError = isError,
                contentDescription = stringResource(Res.string.enter_password),
            )
            PasswordField(
                passwordState = repeatPasswordState,
                label = stringResource(repeatLabelResource),
                isError = isRepeatError,
                contentDescription = stringResource(Res.string.confirm_password),
            )
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    },
    confirmButton = { Button(onClick = onConfirmClick) { Text(stringResource(Res.string.yes)) } },
    dismissButton = { Button(onClick = dismissDialog) { Text(stringResource(Res.string.cancel)) } },
    onDismissRequest = dismissDialog,
)

@Preview
@Composable
fun PreviewConfirmPasswordDialog() = PreviewDialog { ShowConfirmPasswordDialog() }