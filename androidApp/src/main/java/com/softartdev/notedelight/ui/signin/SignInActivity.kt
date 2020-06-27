package com.softartdev.notedelight.ui.signin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.lifecycle.Observer
import com.softartdev.notedelight.R
import com.softartdev.notedelight.ui.base.BaseActivity
import com.softartdev.notedelight.ui.main.MainActivity
import com.softartdev.notedelight.util.gone
import com.softartdev.notedelight.util.hideKeyboard
import com.softartdev.notedelight.util.visible
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.view_error.view.*
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel

class SignInActivity : BaseActivity(R.layout.activity_sign_in), Observer<SignInResult> {

    private val signInViewModel by lifecycleScope.viewModel<SignInViewModel>(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sign_in_error_view.button_reload.setOnClickListener { attemptSignIn() }
        password_edit_text.setOnEditorActionListener { _, _, _ ->
            attemptSignIn()
            true
        }
        sign_in_button.setOnClickListener { attemptSignIn() }
        signInViewModel.resultLiveData.observe(this, this)
    }

    private fun attemptSignIn() {
        hideKeyboard()
        val passphrase: Editable = password_edit_text.editableText
        signInViewModel.signIn(passphrase)
    }

    override fun onChanged(signInResult: SignInResult) = when (signInResult) {
        is SignInResult.ShowProgress -> {
            sign_in_progress_view.visible()
            sign_in_layout.gone()
            sign_in_error_view.gone()
        }
        SignInResult.NavMain -> {
            showSignIn()
            navMain()
        }
        SignInResult.ShowEmptyPassError -> showSignIn(
                errorText = getString(R.string.empty_password)
        )
        SignInResult.ShowIncorrectPassError -> showSignIn(
                errorText = getString(R.string.incorrect_password)
        )
        is SignInResult.ShowError -> {
            sign_in_progress_view.gone()
            sign_in_layout.gone()
            sign_in_error_view.apply {
                text_error_message.text = signInResult.error.message
            }.visible()
        }
    }

    private fun showSignIn(errorText: String? = null) {
        sign_in_progress_view.gone()
        sign_in_layout.apply {
            password_text_input_layout.error = errorText
        }.visible()
        sign_in_error_view.gone()
    }

    private fun navMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}

