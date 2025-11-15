package com.softartdev.notedelight

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.softartdev.notedelight.ui.SplashScreenBody
import com.softartdev.notedelight.ui.main.NoteDetailBody
import com.softartdev.notedelight.ui.main.PreviewAdaptiveScreen
import com.softartdev.notedelight.ui.main.PreviewMainScreen
import com.softartdev.notedelight.ui.settings.PreviewAdaptiveSettingsScreen
import com.softartdev.notedelight.ui.signin.SignInScreenBody
import com.softartdev.theme.material3.PreferableMaterialTheme

/**
 * A MultiPreview annotation for displaying a @[Composable] method using different device types
 * and UI modes.
 */
@Preview(name = "TabletLight", group = "Device & UI-mode", device = Devices.TABLET, uiMode = Configuration.UI_MODE_NIGHT_NO, showSystemUi = true)
@Preview(name = "TabletDark", group = "Device & UI-mode", device = Devices.TABLET, uiMode = Configuration.UI_MODE_NIGHT_YES, showSystemUi = true)
@Preview(name = "PhoneLight", group = "Device & UI-mode", device = Devices.PHONE, uiMode = Configuration.UI_MODE_NIGHT_NO, showSystemUi = true)
@Preview(name = "PhoneDark", group = "Device & UI-mode", device = Devices.PHONE, uiMode = Configuration.UI_MODE_NIGHT_YES, showSystemUi = true)
annotation class PreviewDeviceMode

@PreviewDeviceMode
@Composable
fun SignInScreenPreview() = PreferableMaterialTheme { SignInScreenBody() }

@PreviewDeviceMode
@Composable
fun AdaptiveScreenPreview() = PreferableMaterialTheme { PreviewAdaptiveScreen() }

@PreviewDeviceMode
@Composable
fun AdaptiveSettingsScreenPreview() = PreferableMaterialTheme { PreviewAdaptiveSettingsScreen() }

@PreviewLightDark
@Composable
fun NoteDetailBodyPreview() = PreferableMaterialTheme { NoteDetailBody() }

@PreviewLightDark
@Composable
fun MainScreenPreview() = PreferableMaterialTheme { PreviewMainScreen() }

@PreviewLightDark
@Composable
fun SplashScreenPreview() = PreferableMaterialTheme { SplashScreenBody(true) }
