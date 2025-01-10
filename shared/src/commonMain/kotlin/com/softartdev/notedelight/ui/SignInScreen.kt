@file:OptIn(ExperimentalMaterial3Api::class)

package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.presentation.signin.SignInResult
import com.softartdev.notedelight.presentation.signin.SignInViewModel
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.app_name
import notedelight.shared.generated.resources.empty_password
import notedelight.shared.generated.resources.enter_password
import notedelight.shared.generated.resources.incorrect_password
import notedelight.shared.generated.resources.sign_in
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignInScreen(signInViewModel: SignInViewModel) {
    val signInResultState: State<SignInResult> = signInViewModel.stateFlow.collectAsState()
    val passwordState: MutableState<String> = remember { mutableStateOf("") }
    SignInScreenBody(
        showLoading = signInResultState.value == SignInResult.ShowProgress,
        passwordState = passwordState,
        labelResource = when (signInResultState.value) {
            SignInResult.ShowEmptyPassError -> Res.string.empty_password
            SignInResult.ShowIncorrectPassError -> Res.string.incorrect_password
            else -> Res.string.enter_password
        },
        isError = signInResultState.value == SignInResult.ShowEmptyPassError
                || signInResultState.value == SignInResult.ShowIncorrectPassError,
    ) { signInViewModel.signIn(pass = passwordState.value) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreenBody(
    showLoading: Boolean = true,
    passwordState: MutableState<String> = mutableStateOf("password"),
    labelResource: StringResource = Res.string.enter_password,
    isError: Boolean = false,
    onSignInClick: () -> Unit = {},
) = Scaffold(
    topBar = { TopAppBar(title = { Text(stringResource(Res.string.app_name)) }) },
) { paddingValues: PaddingValues ->
    Column(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(all = 16.dp)) {
        if (showLoading) LinearProgressIndicator()
        PasswordField(
            modifier = Modifier.fillMaxWidth(),
            passwordState = passwordState,
            label = stringResource(labelResource),
            isError = isError,
            contentDescription = stringResource(Res.string.enter_password)
        )
        Button(
            onClick = onSignInClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) { Text(text = stringResource(Res.string.sign_in)) }
    }
}

@Preview
@Composable
fun PreviewSignInScreen() = SignInScreenBody()