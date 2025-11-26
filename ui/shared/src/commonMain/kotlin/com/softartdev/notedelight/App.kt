package com.softartdev.notedelight

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.softartdev.notedelight.di.PreviewKoin
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.ui.EnableEdgeToEdge
import com.softartdev.notedelight.ui.GlobalSnackbarHost
import com.softartdev.notedelight.ui.SplashScreen
import com.softartdev.notedelight.ui.dialog.EditTitleDialog
import com.softartdev.notedelight.ui.dialog.ErrorDialog
import com.softartdev.notedelight.ui.dialog.LanguageDialog
import com.softartdev.notedelight.ui.dialog.note.DeleteDialog
import com.softartdev.notedelight.ui.dialog.note.SaveDialog
import com.softartdev.notedelight.ui.dialog.security.ChangePasswordDialog
import com.softartdev.notedelight.ui.dialog.security.ConfirmPasswordDialog
import com.softartdev.notedelight.ui.dialog.security.EnterPasswordDialog
import com.softartdev.notedelight.ui.files.FileListScreen
import com.softartdev.notedelight.ui.main.AdaptiveMainScreen
import com.softartdev.notedelight.ui.settings.AdaptiveSettingsScreen
import com.softartdev.notedelight.ui.signin.SignInScreen
import com.softartdev.theme.material3.PreferableMaterialTheme
import com.softartdev.theme.material3.ThemeDialogContent
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun App(
    router: Router = koinInject(),
    navController: NavHostController = rememberNavController(),
) = PreferableMaterialTheme {
    EnableEdgeToEdge()
    DisposableEffect(key1 = router, key2 = navController) {
        router.setController(navController)
        onDispose(router::releaseController)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = AppNavGraph.Splash,
        ) {
            composable<AppNavGraph.Splash> {
                SplashScreen(splashViewModel = koinViewModel())
            }
            composable<AppNavGraph.SignIn> {
                SignInScreen(signInViewModel = koinViewModel())
            }
            composable<AppNavGraph.Main> {
                AdaptiveMainScreen(router)
            }
            composable<AppNavGraph.Settings> {
                AdaptiveSettingsScreen()
            }
            composable<AppNavGraph.FileList> {
                FileListScreen(
                    onBackClick = navController::popBackStack,
                    filesViewModel = koinViewModel()
                )
            }
            dialog<AppNavGraph.ThemeDialog> {
                ThemeDialogContent(dismissDialog = navController::popBackStack)
            }
            dialog<AppNavGraph.LanguageDialog> {
                LanguageDialog(languageViewModel = koinViewModel())
            }
            dialog<AppNavGraph.SaveChangesDialog> {
                SaveDialog(saveViewModel = koinViewModel())
            }
            dialog<AppNavGraph.EditTitleDialog> { backStackEntry: NavBackStackEntry ->
                EditTitleDialog(editTitleViewModel = koinViewModel {
                    parametersOf(backStackEntry.toRoute<AppNavGraph.EditTitleDialog>().noteId)
                })
            }
            dialog<AppNavGraph.DeleteNoteDialog> {
                DeleteDialog(deleteViewModel = koinViewModel())
            }
            dialog<AppNavGraph.EnterPasswordDialog> {
                EnterPasswordDialog(enterViewModel = koinViewModel())
            }
            dialog<AppNavGraph.ConfirmPasswordDialog> {
                ConfirmPasswordDialog(confirmViewModel = koinViewModel())
            }
            dialog<AppNavGraph.ChangePasswordDialog> {
                ChangePasswordDialog(changeViewModel = koinViewModel())
            }
            dialog<AppNavGraph.ErrorDialog> { backStackEntry: NavBackStackEntry ->
                ErrorDialog(
                    message = backStackEntry.toRoute<AppNavGraph.ErrorDialog>().message,
                    dismissDialog = navController::navigateUp
                )
            }
        }
        GlobalSnackbarHost(
            modifier = Modifier.align(Alignment.BottomCenter),
            snackbarInteractor = koinInject(),
        )
    }
}

@Preview
@Composable
fun PreviewApp() = PreviewKoin { App() }