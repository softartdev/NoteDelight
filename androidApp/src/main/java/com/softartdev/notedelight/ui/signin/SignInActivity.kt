package com.softartdev.notedelight.ui.signin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.lifecycle.Observer
import by.kirich1409.viewbindingdelegate.viewBinding
import com.softartdev.notedelight.R
import com.softartdev.notedelight.databinding.ActivitySignInBinding
import com.softartdev.notedelight.ui.base.BaseActivity
import com.softartdev.notedelight.ui.main.MainActivity
import com.softartdev.notedelight.util.gone
import com.softartdev.notedelight.util.hideKeyboard
import com.softartdev.notedelight.util.visible
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignInActivity : BaseActivity(R.layout.activity_sign_in), Observer<SignInResult> {

    private val signInViewModel by viewModel<SignInViewModel>()
    private val binding by viewBinding(ActivitySignInBinding::bind, android.R.id.content)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.signInErrorView.reloadButton.setOnClickListener { attemptSignIn() }
        binding.passwordEditText.setOnEditorActionListener { _, _, _ ->
            attemptSignIn()
            true
        }
        binding.signInButton.setOnClickListener { attemptSignIn() }
        signInViewModel.resultLiveData.observe(this, this)
    }

    private fun attemptSignIn() {
        hideKeyboard()
        val passphrase: Editable = binding.passwordEditText.editableText
        signInViewModel.signIn(passphrase)
    }

    override fun onChanged(signInResult: SignInResult) = when (signInResult) {
        is SignInResult.ShowProgress -> {
            binding.signInProgressView.visible()
            binding.signInLayout.gone()
            binding.signInErrorView.gone()
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
            binding.signInProgressView.gone()
            binding.signInLayout.gone()
            binding.signInErrorView.apply {
                messageTextView.text = signInResult.error.message
            }.visible()
        }
    }

    private fun showSignIn(errorText: String? = null) {
        binding.signInProgressView.gone()
        binding.signInLayout.apply {
            binding.passwordTextInputLayout.error = errorText
        }.visible()
        binding.signInErrorView.gone()
    }

    private fun navMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}

