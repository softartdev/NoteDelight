package com.softartdev.notedelight.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.addRepeatingJob
import com.softartdev.notedelight.ui.base.BaseActivity
import com.softartdev.notedelight.ui.main.MainActivity
import com.softartdev.notedelight.ui.signin.SignInActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity(), Observer<SplashResult> {
    private val splashViewModel by viewModel<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addRepeatingJob(Lifecycle.State.STARTED) {
            splashViewModel.resultStateFlow.onEach(::onChanged).collect()
        }
        splashViewModel.checkEncryption()
    }

    override fun onChanged(splashResult: SplashResult) = when (splashResult) {
        SplashResult.Loading -> Unit//TODO: progress bar
        SplashResult.NavSignIn -> navSignIn()
        SplashResult.NavMain -> navMain()
        is SplashResult.ShowError -> showError(splashResult.message)
    }

    private fun navSignIn() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showError(message: String?) {
        with(AlertDialog.Builder(this)) {
            setTitle(android.R.string.dialog_alert_title)
            setMessage(message)
            setNeutralButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
            show()
        }
    }
}
