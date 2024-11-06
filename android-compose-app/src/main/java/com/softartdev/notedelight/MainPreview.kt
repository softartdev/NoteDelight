package com.softartdev.notedelight

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.softartdev.notedelight.ui.NoteDetailBody
import com.softartdev.notedelight.ui.SignInScreenBody
import com.softartdev.notedelight.ui.SplashScreenBody
import com.softartdev.theme.material3.PreferableMaterialTheme

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewNoteDetailBodyLight() = PreferableMaterialTheme { NoteDetailBody() }
/*TODO
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreviewLight() = PreferableMaterialTheme { PreviewMainScreen() }
*/
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSignInScreenLight() = PreferableMaterialTheme { SignInScreenBody() }

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSplashScreenLight() = PreferableMaterialTheme { SplashScreenBody(true) }

@Preview(showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewNoteDetailBodyDark() = PreferableMaterialTheme { NoteDetailBody() }
/*TODO
@Preview(showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DefaultPreviewDark() = PreferableMaterialTheme { PreviewMainScreen() }
*/
@Preview(showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewSignInScreenDark() = PreferableMaterialTheme { SignInScreenBody() }

@Preview(showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewSplashScreenDark() = PreferableMaterialTheme { SplashScreenBody(true) }