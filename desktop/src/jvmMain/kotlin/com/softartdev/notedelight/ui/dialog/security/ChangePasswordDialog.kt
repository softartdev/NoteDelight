package com.softartdev.notedelight.ui.dialog.security

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.di.AppModule
import com.softartdev.notedelight.shared.presentation.settings.security.change.ChangeResult
import com.softartdev.notedelight.shared.presentation.settings.security.change.ChangeViewModel
import com.softartdev.notedelight.ui.PasswordField
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordDialog(dismissDialog: () -> Unit, appModule: AppModule) {
    val changeViewModel: ChangeViewModel = remember(appModule::changeViewModel)
    val changeResultState: State<ChangeResult> = changeViewModel.resultStateFlow.collectAsState()
    DisposableEffect(changeViewModel) {
        onDispose(changeViewModel::onCleared)
    }
    var oldLabel = MR.strings.enter_old_password.localized()
    var oldError by remember { mutableStateOf(false) }
    val oldPasswordState: MutableState<String> = remember { mutableStateOf("") }
    var newLabel = MR.strings.enter_new_password.localized()
    var newError by remember { mutableStateOf(false) }
    val newPasswordState: MutableState<String> = remember { mutableStateOf("") }
    var repeatLabel = MR.strings.repeat_new_password.localized()
    var repeatError by remember { mutableStateOf(false) }
    val repeatPasswordState: MutableState<String> = remember { mutableStateOf("") }
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    when (val changeResult: ChangeResult = changeResultState.value) {
        is ChangeResult.InitState, is ChangeResult.Loading -> Unit
        is ChangeResult.Success -> dismissDialog()
        is ChangeResult.OldEmptyPasswordError -> {
            oldLabel = MR.strings.empty_password.localized()
            oldError = true
        }
        is ChangeResult.NewEmptyPasswordError -> {
            newLabel = MR.strings.empty_password.localized()
            newError = true
        }
        is ChangeResult.PasswordsNoMatchError -> {
            repeatLabel = MR.strings.passwords_do_not_match.localized()
            repeatError = true
        }
        is ChangeResult.IncorrectPasswordError -> {
            oldLabel = MR.strings.incorrect_password.localized()
            oldError = true
        }
        is ChangeResult.Error -> coroutineScope.launch {
            snackbarHostState.showSnackbar(changeResult.message ?: MR.strings.error_title.localized())
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShowChangePasswordDialog(
    showLoaing: Boolean = true,
    oldLabel: String = MR.strings.enter_old_password.localized(),
    oldError: Boolean = false,
    oldPasswordState: MutableState<String> = mutableStateOf("old password"),
    newLabel: String = MR.strings.enter_new_password.localized(),
    newError: Boolean = false,
    newPasswordState: MutableState<String> = mutableStateOf("new password"),
    repeatLabel: String = MR.strings.repeat_new_password.localized(),
    repeatError: Boolean = true,
    repeatPasswordState: MutableState<String> = mutableStateOf("repeat new password"),
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    dismissDialog: () -> Unit = {},
    onChangeClick: () -> Unit = {},
) = AlertDialog(
    title = { Text(text = MR.strings.dialog_title_change_password.localized()) },
    text = {
        Column {
            if (showLoaing) LinearProgressIndicator()
            PasswordField(
                passwordState = oldPasswordState,
                label = oldLabel,
                isError = oldError,
            )
            PasswordField(
                passwordState = newPasswordState,
                label = newLabel,
                isError = newError,
            )
            PasswordField(
                passwordState = repeatPasswordState,
                label = repeatLabel,
                isError = repeatError,
            )
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    },
    confirmButton = { Button(onClick = onChangeClick) { Text(MR.strings.yes.localized()) } },
    dismissButton = { Button(onClick = dismissDialog) { Text(MR.strings.cancel.localized()) } },
    onDismissRequest = dismissDialog,
)

@Preview
@Composable
fun PreviewChangePasswordDialog() = PreviewDialog { ShowChangePasswordDialog() }