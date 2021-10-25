package com.softartdev.notedelight

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.pop
import com.arkivanov.decompose.push
import com.arkivanov.decompose.router
import com.softartdev.notedelight.di.AppModule
import com.softartdev.notedelight.ui.MainScreen
import com.softartdev.notedelight.ui.NoteDetail
import com.softartdev.notedelight.ui.SettingsScreen

typealias Content = @Composable () -> Unit

class Root(
    componentContext: ComponentContext, // In Decompose each component has its own ComponentContext
    private val appModule: AppModule, // Accept the AppModule as dependency,
    private val darkThemeState: MutableState<Boolean>,
) : ComponentContext by componentContext {

    private val router = router<Configuration, Content>(
        initialConfiguration = Configuration.List, // Starting with List
        childFactory = ::createChild // The Router calls this function, providing the child Configuration and ComponentContext
    )
    val routerState = router.state

    private fun createChild(configuration: Configuration, context: ComponentContext): Content =
        when (configuration) {
            is Configuration.List -> noteList()
            is Configuration.Details -> noteDetail(configuration)
            is Configuration.Settings -> settings()
        } // Configurations are handled exhaustively

    private fun noteList(): Content = {
        MainScreen(
            appModule = appModule, // Supply dependencies
            onItemClicked = { router.push(Configuration.Details(itemId = it)) }, // Push Details on item click
            onSettingsClick = { router.push(Configuration.Settings) },
        )
    }

    private fun noteDetail(configuration: Configuration.Details): Content = {
        NoteDetail(
            noteId = configuration.itemId, // Safely pass arguments
            onBackClick = router::pop, // Supply dependencies
            onSettingsClick = { router.push(Configuration.Settings) },
            appModule = appModule, // Go back to List
        )
    }

    private fun settings(): Content = {
        SettingsScreen(
            onBackClick = router::pop,
            appModule = appModule,
            darkThemeState = darkThemeState
        )
    }
}

@Composable
fun RootUi(root: Root) {
    Children(root.routerState) { child ->
        child.instance()
    }
}
