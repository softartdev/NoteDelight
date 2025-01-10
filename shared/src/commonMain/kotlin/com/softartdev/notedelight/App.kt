package com.softartdev.notedelight

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.ui.MainScreen
import com.softartdev.notedelight.ui.NoteDetail
import com.softartdev.notedelight.ui.SettingsScreen
import com.softartdev.notedelight.ui.SignInScreen
import com.softartdev.notedelight.ui.SplashScreen
import com.softartdev.notedelight.ui.dialog.EditTitleDialog
import com.softartdev.notedelight.ui.dialog.ErrorDialog
import com.softartdev.notedelight.ui.dialog.note.DeleteDialog
import com.softartdev.notedelight.ui.dialog.note.SaveDialog
import com.softartdev.notedelight.ui.dialog.security.ChangePasswordDialog
import com.softartdev.notedelight.ui.dialog.security.ConfirmPasswordDialog
import com.softartdev.notedelight.ui.dialog.security.EnterPasswordDialog
import com.softartdev.theme.material3.PreferableMaterialTheme
import com.softartdev.theme.material3.ThemeDialog
import com.softartdev.theme.pref.PreferenceHelper
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinNavViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun App(
    router: Router,
    navController: NavHostController = rememberNavController()
) = KoinContext {
    WrappedApp(router, navController)
}

@Composable
private fun WrappedApp(
    router: Router,
    navController: NavHostController = rememberNavController()
) = PreferableMaterialTheme {
    DisposableEffect(key1 = router, key2 = navController) {
        router.setController(navController)
        onDispose(router::releaseController)
    }
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
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
            MainScreen(
                mainViewModel = koinViewModel(),
                snackbarHostState = snackbarHostState
            )
        }
        composable<AppNavGraph.Details> { backStackEntry: NavBackStackEntry ->
            NoteDetail(
                noteViewModel = koinNavViewModel {
                    parametersOf(backStackEntry.toRoute<AppNavGraph.Details>().noteId)
                },
                snackbarHostState = snackbarHostState
            )
        }
        composable<AppNavGraph.Settings> {
            SettingsScreen(
                settingsViewModel = koinViewModel(),
                snackbarHostState = snackbarHostState
            )
        }
        dialog<AppNavGraph.ThemeDialog> {
            val preferenceHelper: PreferenceHelper = themePrefs.preferenceHelper
            ThemeDialog(
                darkThemeState = themePrefs.darkThemeState,
                writePref = preferenceHelper::themeEnum::set,
                dismissDialog = navController::navigateUp,
            )
        }
        dialog<AppNavGraph.SaveChangesDialog> {
            SaveDialog(saveViewModel = koinViewModel())
        }
        dialog<AppNavGraph.EditTitleDialog> { backStackEntry: NavBackStackEntry ->
            EditTitleDialog(
                editTitleViewModel = koinViewModel {
                    parametersOf(backStackEntry.toRoute<AppNavGraph.EditTitleDialog>().noteId)
                },
                snackbarHostState = snackbarHostState
            )
        }
        dialog<AppNavGraph.DeleteNoteDialog> {
            DeleteDialog(deleteViewModel = koinViewModel())
        }
        dialog<AppNavGraph.EnterPasswordDialog> {
            EnterPasswordDialog(
                enterViewModel = koinViewModel(),
                snackbarHostState = snackbarHostState
            )
        }
        dialog<AppNavGraph.ConfirmPasswordDialog> {
            ConfirmPasswordDialog(
                confirmViewModel = koinViewModel(),
                snackbarHostState = snackbarHostState
            )
        }
        dialog<AppNavGraph.ChangePasswordDialog> {
            ChangePasswordDialog(
                changeViewModel = koinViewModel(),
                snackbarHostState = snackbarHostState
            )
        }
        dialog<AppNavGraph.ErrorDialog> { backStackEntry: NavBackStackEntry ->
            ErrorDialog(
                message = backStackEntry.toRoute<AppNavGraph.ErrorDialog>().message,
                dismissDialog = navController::navigateUp
            )
        }
    }
}
