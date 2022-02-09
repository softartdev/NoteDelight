package com.softartdev.notedelight.ui.dialog.security

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.softartdev.annotation.Preview
import com.softartdev.mr.composeLocalized
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.shared.presentation.settings.security.confirm.ConfirmResult
import com.softartdev.notedelight.shared.presentation.settings.security.confirm.ConfirmViewModel
import com.softartdev.notedelight.ui.PasswordField
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import com.softartdev.themepref.AlertDialog
import kotlinx.coroutines.launch

@Composable
fun ConfirmPasswordDialog(dismissDialog: () -> Unit, confirmViewModel: ConfirmViewModel) {
    val confirmResultState: State<ConfirmResult> = confirmViewModel.resultStateFlow.collectAsState()
    DisposableEffect(confirmViewModel) {
        onDispose(confirmViewModel::onCleared)
    }
    var label by remember { mutableStateOf(MR.strings.enter_password.contextLocalized()) }
    var error by remember { mutableStateOf(false) }
    var repeatLabel by remember { mutableStateOf(MR.strings.confirm_password.contextLocalized()) }
    var repeatError by remember { mutableStateOf(false) }
    val passwordState: MutableState<String> = remember { mutableStateOf("") }
    val repeatPasswordState: MutableState<String> = remember { mutableStateOf("") }
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    when (val confirmResult: ConfirmResult = confirmResultState.value) {
        is ConfirmResult.InitState, is ConfirmResult.Loading -> Unit
        is ConfirmResult.Success -> dismissDialog()
        is ConfirmResult.EmptyPasswordError -> {
            label = MR.strings.empty_password.composeLocalized()
            error = true
        }
        is ConfirmResult.PasswordsNoMatchError -> {
            repeatLabel = MR.strings.passwords_do_not_match.composeLocalized()
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
    label: String = MR.strings.enter_password.composeLocalized(),
    repeatLabel: String = MR.strings.confirm_password.composeLocalized(),
    isError: Boolean = false,
    isRepeatError: Boolean = true,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    dismissDialog: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
) = AlertDialog(
    title = { Text(text = MR.strings.dialog_title_conform_password.composeLocalized()) },
    text = {
        Column {
            if (showLoaing) LinearProgressIndicator()
            PasswordField(
                passwordState = passwordState,
                label = label,
                isError = isError,
                contentDescription = MR.strings.enter_password.composeLocalized(),
            )
            PasswordField(
                passwordState = repeatPasswordState,
                label = repeatLabel,
                isError = isRepeatError,
                contentDescription = MR.strings.confirm_password.composeLocalized(),
            )
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    },
    confirmButton = { Button(onClick = onConfirmClick) { Text(MR.strings.yes.composeLocalized()) } },
    dismissButton = { Button(onClick = dismissDialog) { Text(MR.strings.cancel.composeLocalized()) } },
    onDismissRequest = dismissDialog,
)

@Preview
@Composable
fun PreviewConfirmPasswordDialog() = PreviewDialog { ShowConfirmPasswordDialog() }