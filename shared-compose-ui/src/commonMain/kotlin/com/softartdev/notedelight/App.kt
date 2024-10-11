package com.softartdev.notedelight

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
        startDestination = AppNavGraph.Splash.route,
    ) {
        composable(route = AppNavGraph.Splash.route) {
            SplashScreen(splashViewModel = getViewModel())
        }
        composable(route = AppNavGraph.SignIn.route) {
            SignInScreen(signInViewModel = getViewModel())
        }
        composable(route = AppNavGraph.Main.route) {
            MainScreen(mainViewModel = getViewModel())
        }
        composable(
            route = AppNavGraph.Details.route,
            arguments = listOf(navArgument(name = AppNavGraph.ARG_NOTE_ID) { type = NavType.LongType })
        ) { backStackEntry: NavBackStackEntry ->
            NoteDetail(
                noteViewModel = getViewModel(),
                noteId = AppNavGraph.ARG_NOTE_ID.let(backStackEntry.arguments!!::getLong),
            )
        }
        composable(route = AppNavGraph.Settings.route) {
            SettingsScreen(settingsViewModel = getViewModel())
        }
        dialog(route = AppNavGraph.ThemeDialog.route) {
            val preferenceHelper: PreferenceHelper = themePrefs.preferenceHelper
            ThemeDialog(
                darkThemeState = themePrefs.darkThemeState,
                writePref = preferenceHelper::themeEnum::set,
                dismissDialog = navController::navigateUp,
            )
        }
        dialog(route = AppNavGraph.SaveChangesDialog.route) {
            SaveDialog(saveViewModel = getViewModel())
        }
        dialog(
            route = AppNavGraph.EditTitleDialog.route,
            arguments = listOf(navArgument(name = AppNavGraph.ARG_NOTE_ID) { type = NavType.LongType })
        ) { backStackEntry: NavBackStackEntry ->
            EditTitleDialog(
                noteId = AppNavGraph.ARG_NOTE_ID.let(backStackEntry.arguments!!::getLong),
                editTitleViewModel = getViewModel()
            )
        }
        dialog(route = AppNavGraph.DeleteNoteDialog.route) {
            DeleteDialog(deleteViewModel = getViewModel())
        }
        dialog(route = AppNavGraph.EnterPasswordDialog.route) {
            EnterPasswordDialog(enterViewModel = getViewModel())
        }
        dialog(route = AppNavGraph.ConfirmPasswordDialog.route) {
            ConfirmPasswordDialog(confirmViewModel = getViewModel())
        }
        dialog(route = AppNavGraph.ChangePasswordDialog.route) {
            ChangePasswordDialog(changeViewModel = getViewModel())
        }
        dialog(
            route = AppNavGraph.ErrorDialog.route,
            arguments = listOf(navArgument(name = AppNavGraph.ARG_MESSAGE) {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry: NavBackStackEntry ->
            ErrorDialog(
                message = AppNavGraph.ARG_MESSAGE.let(backStackEntry.arguments!!::getString),
                dismissDialog = navController::navigateUp
            )
        }
    }
}