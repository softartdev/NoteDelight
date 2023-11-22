@file:OptIn(ExperimentalMaterial3Api::class)

package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.shared.presentation.signin.SignInResult
import com.softartdev.notedelight.shared.presentation.signin.SignInViewModel
import com.softartdev.notedelight.ui.dialog.showError
import com.softartdev.theme.pref.DialogHolder
import com.softartdev.theme.pref.PreferableMaterialTheme.themePrefs
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SignInScreen(signInViewModel: SignInViewModel, navMain: () -> Unit) {
    val signInResultState: State<SignInResult> = signInViewModel.resultStateFlow.collectAsState()
    DisposableEffect(signInViewModel) {
        onDispose(signInViewModel::onCleared)
    }
    var labelResource by remember { mutableStateOf(MR.strings.enter_password) }
    var error by remember { mutableStateOf(false) }
    val passwordState: MutableState<String> = remember { mutableStateOf("") }
    val dialogHolder: DialogHolder = themePrefs.dialogHolder
    when (val signInResult: SignInResult = signInResultState.value) {
        is SignInResult.ShowSignInForm, is SignInResult.ShowProgress -> Unit
        is SignInResult.NavMain -> navMain()
        is SignInResult.ShowEmptyPassError -> {
            labelResource = MR.strings.empty_password
            error = true
        }
        is SignInResult.ShowIncorrectPassError -> {
            labelResource = MR.strings.incorrect_password
            error = true
        }
        is SignInResult.ShowError -> dialogHolder.showError(signInResult.error.message)
    }
    SignInScreenBody(
        showLoading = signInResultState.value is SignInResult.ShowProgress,
        passwordState = passwordState,
        labelResource = labelResource,
        isError = error,
    ) { signInViewModel.signIn(pass = passwordState.value) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreenBody(
    showLoading: Boolean = true,
    passwordState: MutableState<String> = mutableStateOf("password"),
    labelResource: StringResource = MR.strings.enter_password,
    isError: Boolean = false,
    onSignInClick: () -> Unit = {},
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(text = stringResource(MR.strings.app_name)) },
        )
    }
) {
    Box(modifier = Modifier.padding(it)) {
        Column(modifier = Modifier.fillMaxSize().padding(all = 16.dp)) {
            if (showLoading) LinearProgressIndicator()
            PasswordField(
                modifier = Modifier.fillMaxWidth(),
                passwordState = passwordState,
                label = stringResource(labelResource),
                isError = isError,
                contentDescription = stringResource(MR.strings.enter_password)
            )
            Button(
                onClick = onSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) { Text(text = stringResource(MR.strings.sign_in)) }
        }
        themePrefs.showDialogIfNeed()
    }
}

@Preview
@Composable
fun PreviewSignInScreen() = SignInScreenBody()