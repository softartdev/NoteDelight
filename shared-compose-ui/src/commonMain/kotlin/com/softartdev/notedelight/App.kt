package com.softartdev.notedelight

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.softartdev.notedelight.di.getViewModel
import com.softartdev.notedelight.shared.navigation.AppNavGraph
import com.softartdev.notedelight.shared.navigation.Router
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

@Composable
fun App(
    router: Router,
    navController: NavHostController = rememberNavController()
) = PreferableMaterialTheme {
    DisposableEffect(key1 = router, key2 = navController) {
        router.setController(navController)
        onDispose(router::releaseController)
    }
    NavHost(
        navController = navController,
        startDestination = AppNavGraph.Splash,
    ) {
        composable<AppNavGraph.Splash> {
            SplashScreen(splashViewModel = getViewModel())
        }
        composable<AppNavGraph.SignIn> {
            SignInScreen(signInViewModel = getViewModel())
        }
        composable<AppNavGraph.Main> {
            MainScreen(mainViewModel = getViewModel())
        }
        composable<AppNavGraph.Details> { backStackEntry: NavBackStackEntry ->
            NoteDetail(
                noteViewModel = getViewModel(),
                noteId = backStackEntry.toRoute<AppNavGraph.Details>().noteId,
            )
        }
        composable<AppNavGraph.Settings> {
            SettingsScreen(settingsViewModel = getViewModel())
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
            SaveDialog(saveViewModel = getViewModel())
        }
        dialog<AppNavGraph.EditTitleDialog> { backStackEntry: NavBackStackEntry ->
            EditTitleDialog(
                noteId = backStackEntry.toRoute<AppNavGraph.EditTitleDialog>().noteId,
                editTitleViewModel = getViewModel()
            )
        }
        dialog<AppNavGraph.DeleteNoteDialog> {
            DeleteDialog(deleteViewModel = getViewModel())
        }
        dialog<AppNavGraph.EnterPasswordDialog> {
            EnterPasswordDialog(enterViewModel = getViewModel())
        }
        dialog<AppNavGraph.ConfirmPasswordDialog> {
            ConfirmPasswordDialog(confirmViewModel = getViewModel())
        }
        dialog<AppNavGraph.ChangePasswordDialog> {
            ChangePasswordDialog(changeViewModel = getViewModel())
        }
        dialog<AppNavGraph.ErrorDialog> { backStackEntry: NavBackStackEntry ->
            ErrorDialog(
                message = backStackEntry.toRoute<AppNavGraph.ErrorDialog>().message,
                dismissDialog = navController::navigateUp
            )
        }
    }
}