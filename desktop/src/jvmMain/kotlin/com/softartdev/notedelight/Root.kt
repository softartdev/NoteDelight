package com.softartdev.notedelight

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.arkivanov.decompose.*
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.softartdev.notedelight.di.AppModule
import com.softartdev.notedelight.ui.*

typealias Content = @Composable () -> Unit

class Root(
    componentContext: ComponentContext, // In Decompose each component has its own ComponentContext
    private val appModule: AppModule, // Accept the AppModule as dependency,
    private val darkThemeState: MutableState<Boolean>,
) : ComponentContext by componentContext {

    private val router = router<Configuration, Content>(
        initialConfiguration = Configuration.Splash, // Starting with List
        childFactory = ::createChild // The Router calls this function, providing the child Configuration and ComponentContext
    )
    val routerState = router.state

    private fun createChild(configuration: Configuration, context: ComponentContext): Content =
        when (configuration) {
            is Configuration.Splash -> splash()
            is Configuration.SignIn -> signIn()
            is Configuration.Main -> mainList()
            is Configuration.Details -> noteDetail(configuration)
            is Configuration.Settings -> settings()
        } // Configurations are handled exhaustively

    private fun splash(): Content = {
        SplashScreen(
            appModule = appModule,
            navSignIn = { router.replaceCurrent(Configuration.SignIn) },
            navMain = { router.replaceCurrent(Configuration.Main) },
        )
    }

    private fun signIn(): Content = {
        SignInScreen(
            appModule = appModule,
            navMain = { router.replaceCurrent(Configuration.Main) },
        )
    }

    private fun mainList(): Content = {
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
