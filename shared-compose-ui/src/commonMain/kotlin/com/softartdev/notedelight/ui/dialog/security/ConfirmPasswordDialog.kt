package com.softartdev.notedelight.ui.dialog.security

import com.softartdev.annotation.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.softartdev.mr.localized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.di.AppModule
import com.softartdev.notedelight.shared.presentation.settings.security.confirm.ConfirmResult
import com.softartdev.notedelight.shared.presentation.settings.security.confirm.ConfirmViewModel
import com.softartdev.notedelight.ui.PasswordField
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import com.softartdev.notedelight.util.AlertDialog
import kotlinx.coroutines.launch

@Composable
fun ConfirmPasswordDialog(dismissDialog: () -> Unit, appModule: AppModule) {
    val confirmViewModel: ConfirmViewModel = remember(appModule::confirmViewModel)
    val confirmResultState: State<ConfirmResult> = confirmViewModel.resultStateFlow.collectAsState()
    DisposableEffect(confirmViewModel) {
        onDispose(confirmViewModel::onCleared)
    }
    var label = MR.strings.enter_password.localized()
    var error by remember { mutableStateOf(false) }
    var repeatLabel = MR.strings.confirm_password.localized()
    var repeatError by remember { mutableStateOf(false) }
    val passwordState: MutableState<String> = remember { mutableStateOf("") }
    val repeatPasswordState: MutableState<String> = remember { mutableStateOf("") }
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    when (val confirmResult: ConfirmResult = confirmResultState.value) {
        is ConfirmResult.InitState, is ConfirmResult.Loading -> Unit
        is ConfirmResult.Success -> dismissDialog()
        is ConfirmResult.EmptyPasswordError -> {
            label = MR.strings.empty_password.localized()
            error = true
        }
        is ConfirmResult.PasswordsNoMatchError -> {
            repeatLabel = MR.strings.passwords_do_not_match.localized()
            repeatError = true
        }
        is ConfirmResult.Error -> coroutineScope.launch {
            snackbarHostState.showSnackbar(confirmResult.message ?: MR.strings.error_title.localized())
        }
    }
    ShowConfirmPasswordDialog(
        showLoaing = confirmResultState.value is ConfirmResult.Loading,
        passwordState = passwordState,
        repeatPasswordState = repeatPasswordState,
        label = label,
        repeatLabel = repeatLabel,
        isError = error,
        isRepeatError = repeatError,
        snackbarHostState = snackbarHostState,
        dismissDialog = dismissDialog
    ) { confirmViewModel.conformCheck(password = passwordState.value, repeatPassword = repeatPasswordState.value) }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShowConfirmPasswordDialog(
    showLoaing: Boolean = true,
    passwordState: MutableState<String> = mutableStateOf("password"),
    repeatPasswordState: MutableState<String> = mutableStateOf("repeat password"),
    label: String = MR.strings.enter_password.localized(),
    repeatLabel: String = MR.strings.confirm_password.localized(),
    isError: Boolean = false,
    isRepeatError: Boolean = true,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    dismissDialog: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
) = AlertDialog(
    title = { Text(text = MR.strings.dialog_title_conform_password.localized()) },
    text = {
        Column {
            if (showLoaing) LinearProgressIndicator()
            PasswordField(
                passwordState = passwordState,
                label = label,
                isError = isError,
            )
            PasswordField(
                passwordState = repeatPasswordState,
                label = repeatLabel,
                isError = isRepeatError,
            )
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    },
    confirmButton = { Button(onClick = onConfirmClick) { Text(MR.strings.yes.localized()) } },
    dismissButton = { Button(onClick = dismissDialog) { Text(MR.strings.cancel.localized()) } },
    onDismissRequest = dismissDialog,
)

@Preview
@Composable
fun PreviewConfirmPasswordDialog() = PreviewDialog { ShowConfirmPasswordDialog() }