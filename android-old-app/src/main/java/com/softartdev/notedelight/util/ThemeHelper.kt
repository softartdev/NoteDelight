package com.softartdev.notedelight.util

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.softartdev.notedelight.R

object ThemeHelper {

    private val isAtLeastQ
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun applyTheme(themePref: String, context: Context) = AppCompatDelegate.setDefaultNightMode(when (themePref) {
        context.getString(R.string.light_theme_entry) -> AppCompatDelegate.MODE_NIGHT_NO
        context.getString(R.string.dark_theme_entry) -> AppCompatDelegate.MODE_NIGHT_YES
        else -> if (isAtLeastQ) AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM else AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
    })
}
