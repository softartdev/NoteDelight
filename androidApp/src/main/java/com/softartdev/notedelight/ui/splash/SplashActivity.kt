package com.softartdev.notedelight.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.softartdev.notedelight.ui.main.MainActivity
import com.softartdev.notedelight.ui.signin.SignInActivity
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel

class SplashActivity : AppCompatActivity(), Observer<SplashResult> {
    private val splashViewModel by lifecycleScope.viewModel<SplashViewModel>(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashViewModel.resultLiveData.observe(this, this)
    }

    override fun onChanged(splashResult: SplashResult) = when (splashResult) {
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
