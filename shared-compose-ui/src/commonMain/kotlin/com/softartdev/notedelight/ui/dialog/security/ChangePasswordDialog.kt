package com.softartdev.notedelight.ui.dialog.security

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.softartdev.mr.composeLocalized
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.shared.presentation.settings.security.change.ChangeResult
import com.softartdev.notedelight.shared.presentation.settings.security.change.ChangeViewModel
import com.softartdev.notedelight.ui.PasswordField
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import com.softartdev.themepref.AlertDialog
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordDialog(dismissDialog: () -> Unit, changeViewModel: ChangeViewModel) {
    val changeResultState: State<ChangeResult> = changeViewModel.resultStateFlow.collectAsState()
    DisposableEffect(changeViewModel) {
        onDispose(changeViewModel::onCleared)
    }
    var oldLabel = MR.strings.enter_old_password.composeLocalized()
    var oldError by remember { mutableStateOf(false) }
    val oldPasswordState: MutableState<String> = remember { mutableStateOf("") }
    var newLabel = MR.strings.enter_new_password.composeLocalized()
    var newError by remember { mutableStateOf(false) }
    val newPasswordState: MutableState<String> = remember { mutableStateOf("") }
    var repeatLabel = MR.strings.repeat_new_password.composeLocalized()
    var repeatError by remember { mutableStateOf(false) }
    val repeatPasswordState: MutableState<String> = remember { mutableStateOf("") }
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    when (val changeResult: ChangeResult = changeResultState.value) {
        is ChangeResult.InitState, is ChangeResult.Loading -> Unit
        is ChangeResult.Success -> dismissDialog()
        is ChangeResult.OldEmptyPasswordError -> {
            oldLabel = MR.strings.empty_password.composeLocalized()
            oldError = true
        }
        is ChangeResult.NewEmptyPasswordError -> {
            newLabel = MR.strings.empty_password.composeLocalized()
            newError = true
        }
        is ChangeResult.PasswordsNoMatchError -> {
            repeatLabel = MR.strings.passwords_do_not_match.composeLocalized()
            repeatError = true
        }
        is ChangeResult.IncorrectPasswordError -> {
            oldLabel = MR.strings.incorrect_password.composeLocalized()
            oldError = true
        }
        is ChangeResult.Error -> coroutineScope.launch {
            snackbarHostState.showSnackbar(changeResult.message ?: MR.strings.error_title.contextLocalized())
        }
    }
    ShowChangePasswordDialog(
        showLoaing = changeResultState.value is ChangeResult.Loading,
        oldLabel = oldLabel,
        oldError = oldError,
        oldPasswordState = oldPasswordState,
        newLabel = newLabel,
        newError = newError,
        newPasswordState = newPasswordState,
        repeatLabel = repeatLabel,
        repeatError = repeatError,
        repeatPasswordState = repeatPasswordState,
        snackbarHostState = snackbarHostState,
        dismissDialog = dismissDialog
    ) { changeViewModel.checkChange(oldPassword = oldPasswordState.value, newPassword = newPasswordState.value, repeatNewPassword = repeatPasswordState.value) }
}

@Composable
fun ShowChangePasswordDialog(
    showLoaing: Boolean = true,
    oldLabel: String = MR.strings.enter_old_password.composeLocalized(),
    oldError: Boolean = false,
    oldPasswordState: MutableState<String> = mutableStateOf("old password"),
    newLabel: String = MR.strings.enter_new_password.composeLocalized(),
    newError: Boolean = false,
    newPasswordState: MutableState<String> = mutableStateOf("new password"),
    repeatLabel: String = MR.strings.repeat_new_password.composeLocalized(),
    repeatError: Boolean = true,
    repeatPasswordState: MutableState<String> = mutableStateOf("repeat new password"),
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    dismissDialog: () -> Unit = {},
    onChangeClick: () -> Unit = {},
) = AlertDialog(
    title = { Text(text = MR.strings.dialog_title_change_password.composeLocalized()) },
    text = {
        Column {
            if (showLoaing) LinearProgressIndicator()
            PasswordField(
                passwordState = oldPasswordState,
                label = oldLabel,
                isError = oldError,
                contentDescription = MR.strings.enter_old_password.composeLocalized(),
            )
            PasswordField(
                passwordState = newPasswordState,
                label = newLabel,
                isError = newError,
                contentDescription = MR.strings.enter_new_password.composeLocalized(),
            )
            PasswordField(
                passwordState = repeatPasswordState,
                label = repeatLabel,
                isError = repeatError,
                contentDescription = MR.strings.repeat_new_password.composeLocalized(),
            )
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    },
    confirmButton = { Button(onClick = onChangeClick) { Text(MR.strings.yes.composeLocalized()) } },
    dismissButton = { Button(onClick = dismissDialog) { Text(MR.strings.cancel.composeLocalized()) } },
    onDismissRequest = dismissDialog,
)

@Preview
@Composable
fun PreviewChangePasswordDialog() = PreviewDialog { ShowChangePasswordDialog() }