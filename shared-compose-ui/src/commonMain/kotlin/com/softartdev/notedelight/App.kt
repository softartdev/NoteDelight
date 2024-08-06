package com.softartdev.notedelight

import androidx.compose.runtime.Composable
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
import com.softartdev.notedelight.ui.dialog.security.EnterPasswordDialog
import com.softartdev.theme.material3.PreferableMaterialTheme
import kotlinx.coroutines.launch

@Composable
fun App(navController: NavHostController = rememberNavController()) = PreferableMaterialTheme {
    NavHost(
        navController = navController,
        startDestination = AppNavGraph.Splash.name,
    ) {
        composable(route = AppNavGraph.Splash.name) {
            SplashScreen(
                splashViewModel = getViewModel(),
                navSignIn = {
                    navController.navigate(AppNavGraph.SignIn.name) {
                        popUpTo(AppNavGraph.Splash.name) { inclusive = true }
                    }
                },
                navMain = {
                    navController.navigate(AppNavGraph.Main.name) {
                        popUpTo(AppNavGraph.Splash.name) { inclusive = true }
                    }
                },
            )
        }
        composable(route = AppNavGraph.SignIn.name) {
            SignInScreen(
                signInViewModel = getViewModel(),
                navMain = {
                    navController.navigate(AppNavGraph.Main.name) {
                        popUpTo(AppNavGraph.SignIn.name) { inclusive = true }
                    }
                },
            )
        }
        composable(route = AppNavGraph.Main.name) {
            MainScreen(
                mainViewModel = getViewModel(),
                onItemClicked = { id: Long ->
                    navController.navigate(route = "${AppNavGraph.Details.name}/$id")
                },
                onSettingsClick = {
                    navController.navigate(AppNavGraph.Settings.name)
                },
                navSignIn = {
                    navController.navigate(AppNavGraph.SignIn.name) {
                        popUpTo(AppNavGraph.Main.name) { inclusive = true }
                    }
                },
            )
        }
        composable(
            route = "${AppNavGraph.Details.name}/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStackEntry: NavBackStackEntry ->
            NoteDetail(
                noteViewModel = getViewModel(),
                noteId = backStackEntry.arguments!!.getLong("noteId"),
                navBack = navController::navigateUp
            )
        }
        composable(route = AppNavGraph.Settings.name) {
            SettingsScreen(
                onBackClick = navController::navigateUp,
                settingsViewModel = getViewModel(),
            )
        }
        dialog(route = AppNavGraph.SaveChangesDialog.name) {
            val coroutineScope = rememberCoroutineScope()
            SaveDialog(
                saveNoteAndNavBack = {
                    coroutineScope.launch {
                        SaveNoteUseCase.saveChannel.send(true) //FIXME
                    }
                },
                doNotSaveAndNavBack = {
                    coroutineScope.launch {
                        SaveNoteUseCase.saveChannel.send(false) //FIXME
                    }
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
            DeleteDialog(
                onDeleteClick = { },
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
            EnterPasswordDialog(
                dismissDialog = navController::navigateUp,
                enterViewModel = getViewModel()
            )
        }
        dialog(route = AppNavGraph.ChangePasswordDialog.name) {
            EnterPasswordDialog(
                dismissDialog = navController::navigateUp,
                enterViewModel = getViewModel()
            )
        }
        dialog(route = "${AppNavGraph.ErrorDialog.name}/{message}") { backStackEntry: NavBackStackEntry ->
            ErrorDialog(
                message = backStackEntry.arguments?.getString("message"),
                dismissDialog = navController::navigateUp
            )
        }
    }
}