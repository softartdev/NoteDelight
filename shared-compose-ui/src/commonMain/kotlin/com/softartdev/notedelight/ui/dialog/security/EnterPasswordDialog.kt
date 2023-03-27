package com.softartdev.notedelight.ui.dialog.security

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.shared.presentation.settings.security.enter.EnterResult
import com.softartdev.notedelight.shared.presentation.settings.security.enter.EnterViewModel
import com.softartdev.notedelight.ui.PasswordField
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import com.softartdev.themepref.AlertDialog
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch

@Composable
fun EnterPasswordDialog(dismissDialog: () -> Unit, enterViewModel: EnterViewModel) {
    val enterResultState: State<EnterResult> = enterViewModel.resultStateFlow.collectAsState()
    DisposableEffect(enterViewModel) {
        onDispose(enterViewModel::onCleared)
    }
    var labelResource by remember { mutableStateOf(MR.strings.enter_password) }
    var error by remember { mutableStateOf(false) }
    val passwordState: MutableState<String> = remember { mutableStateOf("") }
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    when (val enterResult: EnterResult = enterResultState.value) {
        is EnterResult.InitState, is EnterResult.Loading -> Unit
        is EnterResult.Success -> dismissDialog()
        is EnterResult.EmptyPasswordError -> {
            labelResource = MR.strings.empty_password
            error = true
        }
        is EnterResult.IncorrectPasswordError -> {
            labelResource = MR.strings.incorrect_password
            error = true
        }
        is EnterResult.Error -> coroutineScope.launch {
            snackbarHostState.showSnackbar(enterResult.message ?: MR.strings.error_title.contextLocalized())
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
    labelResource: StringResource = MR.strings.enter_password,
    isError: Boolean = true,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    dismissDialog: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
) = AlertDialog(
    title = { Text(text = stringResource(MR.strings.dialog_title_enter_password)) },
    text = {
        Column {
            if (showLoaing) LinearProgressIndicator()
            PasswordField(
                passwordState = passwordState,
                label = stringResource(labelResource),
                isError = isError,
                contentDescription = stringResource(MR.strings.enter_password),
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
fun PreviewEnterPasswordDialog() = PreviewDialog { ShowEnterPasswordDialog() }