package com.softartdev.notedelight.ui.dialog.security

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.softartdev.notedelight.shared.presentation.settings.security.change.ChangeResult
import com.softartdev.notedelight.shared.presentation.settings.security.change.ChangeViewModel
import com.softartdev.notedelight.ui.PasswordField
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import kotlinx.coroutines.launch
import notedelight.shared_compose_ui.generated.resources.Res
import notedelight.shared_compose_ui.generated.resources.cancel
import notedelight.shared_compose_ui.generated.resources.dialog_title_change_password
import notedelight.shared_compose_ui.generated.resources.empty_password
import notedelight.shared_compose_ui.generated.resources.enter_new_password
import notedelight.shared_compose_ui.generated.resources.enter_old_password
import notedelight.shared_compose_ui.generated.resources.error_title
import notedelight.shared_compose_ui.generated.resources.incorrect_password
import notedelight.shared_compose_ui.generated.resources.passwords_do_not_match
import notedelight.shared_compose_ui.generated.resources.repeat_new_password
import notedelight.shared_compose_ui.generated.resources.yes
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChangePasswordDialog(dismissDialog: () -> Unit, changeViewModel: ChangeViewModel) {
    val changeResultState: State<ChangeResult> = changeViewModel.resultStateFlow.collectAsState()
    DisposableEffect(changeViewModel) {
        onDispose(changeViewModel::onCleared)
    }
    var oldLabelResource by remember { mutableStateOf(Res.string.enter_old_password) }
    var oldError by remember { mutableStateOf(false) }
    val oldPasswordState: MutableState<String> = remember { mutableStateOf("") }
    var newLabelResource by remember { mutableStateOf(Res.string.enter_new_password) }
    var newError by remember { mutableStateOf(false) }
    val newPasswordState: MutableState<String> = remember { mutableStateOf("") }
    var repeatLabelResource by remember { mutableStateOf(Res.string.repeat_new_password) }
    var repeatError by remember { mutableStateOf(false) }
    val repeatPasswordState: MutableState<String> = remember { mutableStateOf("") }
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    when (val changeResult: ChangeResult = changeResultState.value) {
        is ChangeResult.InitState, is ChangeResult.Loading -> Unit
        is ChangeResult.Success -> dismissDialog()
        is ChangeResult.OldEmptyPasswordError -> {
            oldLabelResource = Res.string.empty_password
            oldError = true
        }
        is ChangeResult.NewEmptyPasswordError -> {
            newLabelResource = Res.string.empty_password
            newError = true
        }
        is ChangeResult.PasswordsNoMatchError -> {
            repeatLabelResource = Res.string.passwords_do_not_match
            repeatError = true
        }
        is ChangeResult.IncorrectPasswordError -> {
            oldLabelResource = Res.string.incorrect_password
            oldError = true
        }
        is ChangeResult.Error -> coroutineScope.launch {
            snackbarHostState.showSnackbar(changeResult.message ?: getString(Res.string.error_title))
        }
    }
    ShowChangePasswordDialog(
        showLoaing = changeResultState.value is ChangeResult.Loading,
        oldLabelResource = oldLabelResource,
        oldError = oldError,
        oldPasswordState = oldPasswordState,
        newLabelResource = newLabelResource,
        newError = newError,
        newPasswordState = newPasswordState,
        repeatLabelResource = repeatLabelResource,
        repeatError = repeatError,
        repeatPasswordState = repeatPasswordState,
        snackbarHostState = snackbarHostState,
        dismissDialog = dismissDialog
    ) { changeViewModel.checkChange(oldPassword = oldPasswordState.value, newPassword = newPasswordState.value, repeatNewPassword = repeatPasswordState.value) }
}

@Composable
fun ShowChangePasswordDialog(
    showLoaing: Boolean = true,
    oldLabelResource: StringResource = Res.string.enter_old_password,
    oldError: Boolean = false,
    oldPasswordState: MutableState<String> = mutableStateOf("old password"),
    newLabelResource: StringResource = Res.string.enter_new_password,
    newError: Boolean = false,
    newPasswordState: MutableState<String> = mutableStateOf("new password"),
    repeatLabelResource: StringResource = Res.string.repeat_new_password,
    repeatError: Boolean = true,
    repeatPasswordState: MutableState<String> = mutableStateOf("repeat new password"),
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    dismissDialog: () -> Unit = {},
    onChangeClick: () -> Unit = {},
) = AlertDialog(
    title = { Text(text = stringResource(Res.string.dialog_title_change_password)) },
    text = {
        Column {
            if (showLoaing) LinearProgressIndicator()
            PasswordField(
                passwordState = oldPasswordState,
                label = stringResource(oldLabelResource),
                isError = oldError,
                contentDescription = stringResource(Res.string.enter_old_password),
            )
            PasswordField(
                passwordState = newPasswordState,
                label = stringResource(newLabelResource),
                isError = newError,
                contentDescription = stringResource(Res.string.enter_new_password),
            )
            PasswordField(
                passwordState = repeatPasswordState,
                label = stringResource(repeatLabelResource),
                isError = repeatError,
                contentDescription = stringResource(Res.string.repeat_new_password),
            )
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    },
    confirmButton = { Button(onClick = onChangeClick) { Text(stringResource(Res.string.yes)) } },
    dismissButton = { Button(onClick = dismissDialog) { Text(stringResource(Res.string.cancel)) } },
    onDismissRequest = dismissDialog,
)

@Preview
@Composable
fun PreviewChangePasswordDialog() = PreviewDialog { ShowChangePasswordDialog() }