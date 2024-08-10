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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.softartdev.notedelight.AppNavGraph
import com.softartdev.notedelight.shared.presentation.signin.SignInResult
import com.softartdev.notedelight.shared.presentation.signin.SignInViewModel
import notedelight.shared_compose_ui.generated.resources.Res
import notedelight.shared_compose_ui.generated.resources.app_name
import notedelight.shared_compose_ui.generated.resources.empty_password
import notedelight.shared_compose_ui.generated.resources.enter_password
import notedelight.shared_compose_ui.generated.resources.incorrect_password
import notedelight.shared_compose_ui.generated.resources.sign_in
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignInScreen(
    signInViewModel: SignInViewModel,
    navController: NavHostController = rememberNavController()
) {
    val signInResultState: State<SignInResult> = signInViewModel.resultStateFlow.collectAsState()
    DisposableEffect(signInViewModel) {
        onDispose(signInViewModel::onCleared)
    }
    var labelResource by remember { mutableStateOf(Res.string.enter_password) }
    var error by remember { mutableStateOf(false) }
    val passwordState: MutableState<String> = remember { mutableStateOf("") }
    when (val signInResult: SignInResult = signInResultState.value) {
        is SignInResult.ShowSignInForm,
        is SignInResult.ShowProgress -> Unit
        is SignInResult.NavMain -> navController.navigate(AppNavGraph.Main.name) {
            popUpTo(AppNavGraph.Main.name) { inclusive = true }
        }
        is SignInResult.ShowEmptyPassError -> {
            labelResource = Res.string.empty_password
            error = true
        }
        is SignInResult.ShowIncorrectPassError -> {
            labelResource = Res.string.incorrect_password
            error = true
        }
        is SignInResult.ShowError -> navController.navigate(
            route = AppNavGraph.ErrorDialog.argRoute(message = signInResult.error.message),
        )
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
    labelResource: StringResource = Res.string.enter_password,
    isError: Boolean = false,
    onSignInClick: () -> Unit = {},
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(text = stringResource(Res.string.app_name)) },
        )
    }
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