package com.softartdev.notedelight.ui.dialog.security

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.shared.presentation.settings.security.confirm.ConfirmResult
import com.softartdev.notedelight.shared.presentation.settings.security.confirm.ConfirmViewModel
import com.softartdev.notedelight.ui.PasswordField
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import com.softartdev.themepref.AlertDialog
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch

@Composable
fun ConfirmPasswordDialog(dismissDialog: () -> Unit, confirmViewModel: ConfirmViewModel) {
    val confirmResultState: State<ConfirmResult> = confirmViewModel.resultStateFlow.collectAsState()
    DisposableEffect(confirmViewModel) {
        onDispose(confirmViewModel::onCleared)
    }
    var labelResource by remember { mutableStateOf(MR.strings.enter_password) }
    var error by remember { mutableStateOf(false) }
    var repeatLabelResource by remember { mutableStateOf(MR.strings.confirm_password) }
    var repeatError by remember { mutableStateOf(false) }
    val passwordState: MutableState<String> = remember { mutableStateOf("") }
    val repeatPasswordState: MutableState<String> = remember { mutableStateOf("") }
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    when (val confirmResult: ConfirmResult = confirmResultState.value) {
        is ConfirmResult.InitState, is ConfirmResult.Loading -> Unit
        is ConfirmResult.Success -> dismissDialog()
        is ConfirmResult.EmptyPasswordError -> {
            labelResource = MR.strings.empty_password
            error = true
        }
        is ConfirmResult.PasswordsNoMatchError -> {
            repeatLabelResource = MR.strings.passwords_do_not_match
            repeatError = true
        }
        is ConfirmResult.Error -> coroutineScope.launch {
            snackbarHostState.showSnackbar(confirmResult.message ?: MR.strings.error_title.contextLocalized())
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
    labelResource: StringResource = MR.strings.enter_password,
    repeatLabelResource: StringResource = MR.strings.confirm_password,
    isError: Boolean = false,
    isRepeatError: Boolean = true,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    dismissDialog: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
) = AlertDialog(
    title = { Text(text = stringResource(MR.strings.dialog_title_conform_password)) },
    text = {
        Column {
            if (showLoaing) LinearProgressIndicator()
            PasswordField(
                passwordState = passwordState,
                label = stringResource(labelResource),
                isError = isError,
                contentDescription = stringResource(MR.strings.enter_password),
            )
            PasswordField(
                passwordState = repeatPasswordState,
                label = stringResource(repeatLabelResource),
                isError = isRepeatError,
                contentDescription = stringResource(MR.strings.confirm_password),
            )
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    },
    confirmButton = { Button(onClick = onConfirmClick) { Text(stringResource(MR.strings.yes)) } },
    dismissButton = { Button(onClick = dismissDialog) { Text(stringResource(MR.strings.cancel)) } },
    onDismissRequest = dismissDialog,
)

@Preview
@Composable
fun PreviewConfirmPasswordDialog() = PreviewDialog { ShowConfirmPasswordDialog() }