package com.softartdev.notedelight.ui.base

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.softartdev.notedelight.R
import com.softartdev.notedelight.ui.settings.SettingsActivity
import com.softartdev.notedelight.util.PreferencesHelper
import org.koin.android.ext.android.inject

abstract class BaseActivity(@LayoutRes contentLayoutId: Int) : AppCompatActivity(contentLayoutId) {

    private val preferencesHelper by inject<PreferencesHelper>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide task snapshot if enable in settings
        if (preferencesHelper.hideScreenContentsEntry) {
            window.setFlags(FLAG_SECURE, FLAG_SECURE)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.action_settings -> {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}