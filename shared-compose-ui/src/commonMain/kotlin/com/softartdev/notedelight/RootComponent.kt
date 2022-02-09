package com.softartdev.notedelight

import com.arkivanov.decompose.*
import com.arkivanov.decompose.router.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.softartdev.notedelight.di.getViewModel
import com.softartdev.notedelight.ui.*

class RootComponent(
    componentContext: ComponentContext,
) : NoteDelightRoot, ComponentContext by componentContext {

    private val router = componentContext.router<Configuration, ContentChild>(
        initialConfiguration = Configuration.Splash,
        handleBackButton = true,
        childFactory = ::createChild
    )
    override val routerState: Value<RouterState<Configuration, ContentChild>> = router.state

    private fun createChild(configuration: Configuration, context: ComponentContext): ContentChild =
        when (configuration) {
            is Configuration.Splash -> splash()
            is Configuration.SignIn -> signIn()
            is Configuration.Main -> mainList()
            is Configuration.Details -> noteDetail(configuration, context)
            is Configuration.Settings -> settings()
        }

    private fun splash(): ContentChild = {
        SplashScreen(
            splashViewModel = getViewModel(),
            navSignIn = { router.replaceCurrent(Configuration.SignIn) },
            navMain = { router.replaceCurrent(Configuration.Main) },
        )
    }

    private fun signIn(): ContentChild = {
        SignInScreen(
            signInViewModel = getViewModel(),
            navMain = { router.replaceCurrent(Configuration.Main) },
        )
    }

    private fun mainList(): ContentChild = {
        MainScreen(
            mainViewModel = getViewModel(),
            onItemClicked = { id -> router.push(Configuration.Details(itemId = id)) },
            onSettingsClick = { router.push(Configuration.Settings) },
        )
    }

    private fun noteDetail(configuration: Configuration.Details, context: ComponentContext): ContentChild = {
        val backWrapper = BackWrapper()
        context.backPressedHandler.register(backWrapper)
        context.lifecycle.doOnDestroy { context.backPressedHandler.unregister(backWrapper) }

        NoteDetail(
            noteViewModel = getViewModel(),
            noteId = configuration.itemId,
            backWrapper = backWrapper,
            navBack = router::pop
        )
    }

    private fun settings(): ContentChild = {
        SettingsScreen(
            onBackClick = router::pop,
            settingsViewModel = getViewModel(),
        )
    }

    class BackWrapper(var handler: (() -> Unit)? = null): () -> Boolean {
        override fun invoke(): Boolean {
            handler?.invoke() ?: return false
            return true
        }
    }
}
