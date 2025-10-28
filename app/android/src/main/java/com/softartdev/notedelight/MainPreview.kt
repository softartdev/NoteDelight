package com.softartdev.notedelight

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.softartdev.notedelight.ui.NoteDetailBody
import com.softartdev.notedelight.ui.PreviewMainScreen
import com.softartdev.notedelight.ui.SignInScreenBody
import com.softartdev.notedelight.ui.SplashScreenBody
import com.softartdev.notedelight.ui.adaptive.PreviewAdaptiveScreen
import com.softartdev.theme.material3.PreferableMaterialTheme

@Preview(name = "Adaptive", group = "light", showSystemUi = true, showBackground = true, device = Devices.TABLET)
@Composable
fun PreviewAdaptiveScreenLight() = PreferableMaterialTheme { PreviewAdaptiveScreen() }

@Preview(name = "Note", group = "light", showSystemUi = true, showBackground = true)
@Composable
fun PreviewNoteDetailBodyLight() = PreferableMaterialTheme { NoteDetailBody() }

@Preview(name = "Main", group = "light", showSystemUi = true, showBackground = true)
@Composable
fun DefaultPreviewLight() = PreferableMaterialTheme { PreviewMainScreen() }

@Preview(name = "Sign In", group = "light", showSystemUi = true, showBackground = true)
@Composable
fun PreviewSignInScreenLight() = PreferableMaterialTheme { SignInScreenBody() }

@Preview(name = "Splash", group = "light", showSystemUi = true, showBackground = true)
@Composable
fun PreviewSplashScreenLight() = PreferableMaterialTheme { SplashScreenBody(true) }

@Preview(name = "Adaptive", group = "dark", showSystemUi = true, showBackground = true, device = Devices.TABLET, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewAdaptiveScreenDark() = PreferableMaterialTheme { PreviewAdaptiveScreen() }

@Preview(name = "Note", group = "dark", showSystemUi = true, showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewNoteDetailBodyDark() = PreferableMaterialTheme { NoteDetailBody() }

@Preview(name = "Main", group = "dark", showSystemUi = true, showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DefaultPreviewDark() = PreferableMaterialTheme { PreviewMainScreen() }

@Preview(name = "Sign In", group = "dark", showSystemUi = true, showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewSignInScreenDark() = PreferableMaterialTheme { SignInScreenBody() }

@Preview(name = "Splash", group = "dark", showSystemUi = true, showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewSplashScreenDark() = PreferableMaterialTheme { SplashScreenBody(true) }
