@file:OptIn(ExperimentalMaterial3Api::class)

package com.softartdev.notedelight.ui.signin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillManager
import androidx.compose.ui.platform.LocalAutofillManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.presentation.signin.SignInAction
import com.softartdev.notedelight.presentation.signin.SignInResult
import com.softartdev.notedelight.presentation.signin.SignInViewModel
import com.softartdev.notedelight.ui.PasswordField
import com.softartdev.notedelight.util.SIGN_IN_BUTTON_TAG
import com.softartdev.notedelight.util.SIGN_IN_PASSWORD_FIELD_TAG
import com.softartdev.notedelight.util.SIGN_IN_PASSWORD_LABEL_TAG
import com.softartdev.notedelight.util.SIGN_IN_PASSWORD_VISIBILITY_TAG
import com.softartdev.notedelight.util.SIGN_IN_SETTINGS_BUTTON_TAG
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.app_name
import notedelight.ui.shared.generated.resources.empty_password
import notedelight.ui.shared.generated.resources.enter_password
import notedelight.ui.shared.generated.resources.incorrect_password
import notedelight.ui.shared.generated.resources.settings
import notedelight.ui.shared.generated.resources.sign_in
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignInScreen(signInViewModel: SignInViewModel) {
    val signInResultState: State<SignInResult> = signInViewModel.stateFlow.collectAsState()
    val passwordState: MutableState<String> = remember { mutableStateOf("") }
    val autofillManager: AutofillManager? = LocalAutofillManager.current
    LaunchedEffect(key1 = signInViewModel, key2 = autofillManager) {
        signInViewModel.autofillManager = autofillManager
    }
    SignInScreenBody(
        showLoading = signInResultState.value == SignInResult.ShowProgress,
        passwordState = passwordState,
        labelResource = when (signInResultState.value) {
            SignInResult.ShowEmptyPassError -> Res.string.empty_password
            SignInResult.ShowIncorrectPassError -> Res.string.incorrect_password
            else -> Res.string.enter_password
        },
        isError = signInResultState.value.isError,
        onAction = signInViewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreenBody(
    showLoading: Boolean = true,
    passwordState: MutableState<String> = mutableStateOf("password"),
    labelResource: StringResource = Res.string.enter_password,
    isError: Boolean = false,
    onAction: (SignInAction) -> Unit = {},
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(stringResource(Res.string.app_name)) },
            actions = {
                IconButton(
                    modifier = Modifier.testTag(SIGN_IN_SETTINGS_BUTTON_TAG),
                    onClick = { onAction(SignInAction.OnSettingsClick) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(Res.string.settings)
                    )
                }
            }
        )
    },
) { paddingValues: PaddingValues ->
    AdaptiveFrame(modifier = Modifier.padding(paddingValues)) { framePaddingValues: PaddingValues ->
        if (showLoading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }
        Column(modifier = Modifier.padding(framePaddingValues)) {
            PasswordField(
                modifier = Modifier.fillMaxWidth(),
                passwordState = passwordState,
                label = stringResource(labelResource),
                isError = isError,
                contentDescription = stringResource(Res.string.enter_password),
                imeAction = ImeAction.Go,
                keyboardActions = KeyboardActions { onAction(SignInAction.OnSignInClick(passwordState.value)) },
                labelTag = SIGN_IN_PASSWORD_LABEL_TAG,
                visibilityTag = SIGN_IN_PASSWORD_VISIBILITY_TAG,
                fieldTag = SIGN_IN_PASSWORD_FIELD_TAG,
            )
            Button(
                modifier = Modifier
                    .testTag(SIGN_IN_BUTTON_TAG)
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                onClick = { onAction(SignInAction.OnSignInClick(passwordState.value)) },
            ) { Text(text = stringResource(Res.string.sign_in)) }
        }
    }
}

@Preview
@Composable
fun PreviewSignInScreen() = SignInScreenBody()