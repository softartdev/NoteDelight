package com.softartdev.notedelight.ui

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.softartdev.theme.pref.PreferableMaterialTheme.themePrefs
import com.softartdev.theme.pref.ThemeEnum

@Composable
actual fun EnableEdgeToEdge() {
    val activity = LocalActivity.current as? ComponentActivity ?: return
    val inDark: Boolean = when (themePrefs.darkThemeState.value) {
        ThemeEnum.Light -> false
        ThemeEnum.Dark -> true
        ThemeEnum.SystemDefault -> isSystemInDarkTheme()
    }
    val scrimLight: Int = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
    val scrimDark: Int = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
    val sbStyle: SystemBarStyle = when {
        inDark -> SystemBarStyle.dark(scrim = scrimDark)
        else -> SystemBarStyle.light(scrim = scrimLight, darkScrim = scrimDark)
    }
    activity.enableEdgeToEdge(statusBarStyle = sbStyle, navigationBarStyle = sbStyle)
}

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) =
    androidx.activity.compose.BackHandler(enabled, onBack)