package com.softartdev.notedelight

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
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
import com.softartdev.notedelight.shared.usecase.note.SaveNoteUseCase
import com.softartdev.notedelight.ui.MainScreen
import com.softartdev.notedelight.ui.NoteDetail
import com.softartdev.notedelight.ui.SettingsScreen
import com.softartdev.notedelight.ui.SignInScreen
import com.softartdev.notedelight.ui.SplashScreen
import com.softartdev.notedelight.ui.dialog.DeleteDialog
import com.softartdev.notedelight.ui.dialog.EditTitleDialog
import com.softartdev.notedelight.ui.dialog.ErrorDialog
import com.softartdev.notedelight.ui.dialog.SaveDialog
import com.softartdev.notedelight.ui.dialog.security.ChangePasswordDialog
import com.softartdev.notedelight.ui.dialog.security.ConfirmPasswordDialog
import com.softartdev.notedelight.ui.dialog.security.EnterPasswordDialog
import com.softartdev.theme.material3.PreferableMaterialTheme
import com.softartdev.theme.material3.ThemeDialog
import com.softartdev.theme.pref.PreferenceHelper
import kotlinx.coroutines.launch

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
        startDestination = AppNavGraph.Splash.name,
    ) {
        composable(route = AppNavGraph.Splash.name) {
            SplashScreen(splashViewModel = getViewModel())
        }
        composable(route = AppNavGraph.SignIn.name) {
            SignInScreen(signInViewModel = getViewModel())
        }
        composable(route = AppNavGraph.Main.name) {
            MainScreen(
                mainViewModel = getViewModel(),
                navController = navController
            )
        }
        composable(
            route = "${AppNavGraph.Details.name}/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStackEntry: NavBackStackEntry ->
            NoteDetail(
                noteViewModel = getViewModel(),
                noteId = backStackEntry.arguments!!.getLong("noteId"),
                navController = navController
            )
        }
        composable(route = AppNavGraph.Settings.name) {
            SettingsScreen(
                settingsViewModel = getViewModel(),
                navController = navController
            )
        }
        dialog(route = AppNavGraph.ThemeDialog.name) {
            val preferenceHelper: PreferenceHelper = themePrefs.preferenceHelper
            ThemeDialog(
                darkThemeState = themePrefs.darkThemeState,
                writePref = preferenceHelper::themeEnum::set,
                dismissDialog = navController::navigateUp,
            )
        }
        dialog(route = AppNavGraph.SaveChangesDialog.name) {
            val coroutineScope = rememberCoroutineScope()
            SaveDialog(
                saveNoteAndNavBack = {
                    coroutineScope.launch {
                        SaveNoteUseCase.saveChannel.send(true) //FIXME
                    }
                    navController.navigateUp()
                },
                doNotSaveAndNavBack = {
                    coroutineScope.launch {
                        SaveNoteUseCase.saveChannel.send(false) //FIXME
                    }
                    navController.navigateUp()
                },
                onDismiss = navController::navigateUp
            )
        }
        dialog(
            route = "${AppNavGraph.EditTitleDialog.name}/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStackEntry: NavBackStackEntry ->
            EditTitleDialog(
                noteId = backStackEntry.arguments!!.getLong("noteId"),
                dismissDialog = navController::navigateUp,
                editTitleViewModel = getViewModel()
            )
        }
        dialog(route = AppNavGraph.DeleteNoteDialog.name) {
            val coroutineScope = rememberCoroutineScope()
            DeleteDialog(
                onDeleteClick = {
                    coroutineScope.launch {
                        SaveNoteUseCase.deleteChannel.send(true) //FIXME
                    }
                },
                onDismiss = navController::navigateUp
            )
        }
        dialog(route = AppNavGraph.EnterPasswordDialog.name) {
            EnterPasswordDialog(
                dismissDialog = navController::navigateUp,
                enterViewModel = getViewModel()
            )
        }
        dialog(route = AppNavGraph.ConfirmPasswordDialog.name) {
            ConfirmPasswordDialog(
                dismissDialog = navController::navigateUp,
                confirmViewModel = getViewModel()
            )
        }
        dialog(route = AppNavGraph.ChangePasswordDialog.name) {
            ChangePasswordDialog(
                dismissDialog = navController::navigateUp,
                changeViewModel = getViewModel()
            )
        }
        dialog(
            route = "${AppNavGraph.ErrorDialog.name}?message={message}",
            arguments = listOf(navArgument("message") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry: NavBackStackEntry ->
            ErrorDialog(
                message = backStackEntry.arguments?.getString("message"),
                dismissDialog = navController::navigateUp
            )
        }
    }
}