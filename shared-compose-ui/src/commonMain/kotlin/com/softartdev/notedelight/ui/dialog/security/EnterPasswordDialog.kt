package com.softartdev.notedelight.ui.dialog.security

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.softartdev.notedelight.shared.presentation.settings.security.enter.EnterResult
import com.softartdev.notedelight.shared.presentation.settings.security.enter.EnterViewModel
import com.softartdev.notedelight.ui.PasswordField
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import kotlinx.coroutines.launch
import notedelight.shared_compose_ui.generated.resources.Res
import notedelight.shared_compose_ui.generated.resources.cancel
import notedelight.shared_compose_ui.generated.resources.dialog_title_enter_password
import notedelight.shared_compose_ui.generated.resources.empty_password
import notedelight.shared_compose_ui.generated.resources.enter_password
import notedelight.shared_compose_ui.generated.resources.error_title
import notedelight.shared_compose_ui.generated.resources.incorrect_password
import notedelight.shared_compose_ui.generated.resources.yes
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun EnterPasswordDialog(dismissDialog: () -> Unit, enterViewModel: EnterViewModel) {
    val enterResultState: State<EnterResult> = enterViewModel.resultStateFlow.collectAsState()
    DisposableEffect(enterViewModel) {
        onDispose(enterViewModel::onCleared)
    }
    var labelResource by remember { mutableStateOf(Res.string.enter_password) }
    var error by remember { mutableStateOf(false) }
    val passwordState: MutableState<String> = remember { mutableStateOf("") }
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    when (val enterResult: EnterResult = enterResultState.value) {
        is EnterResult.InitState, is EnterResult.Loading -> Unit
        is EnterResult.Success -> dismissDialog()
        is EnterResult.EmptyPasswordError -> {
            labelResource = Res.string.empty_password
            error = true
        }
        is EnterResult.IncorrectPasswordError -> {
            labelResource = Res.string.incorrect_password
            error = true
        }
        is EnterResult.Error -> coroutineScope.launch {
            snackbarHostState.showSnackbar(enterResult.message ?: getString(Res.string.error_title))
        }
    }
    ShowEnterPasswordDialog(
        showLoaing = enterResultState.value is EnterResult.Loading,
        passwordState = passwordState,
        labelResource = labelResource,
        isError = error,
        snackbarHostState = snackbarHostState,
        dismissDialog = dismissDialog
    ) { enterViewModel.enterCheck(password = passwordState.value) }
}

@Composable
fun ShowEnterPasswordDialog(
    showLoaing: Boolean = true,
    passwordState: MutableState<String> = mutableStateOf("password"),
    labelResource: StringResource = Res.string.enter_password,
    isError: Boolean = true,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    dismissDialog: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
) = AlertDialog(
    title = { Text(text = stringResource(Res.string.dialog_title_enter_password)) },
    text = {
        Column {
            if (showLoaing) LinearProgressIndicator()
            PasswordField(
                passwordState = passwordState,
                label = stringResource(labelResource),
                isError = isError,
                contentDescription = stringResource(Res.string.enter_password),
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
fun PreviewEnterPasswordDialog() = PreviewDialog { ShowEnterPasswordDialog() }