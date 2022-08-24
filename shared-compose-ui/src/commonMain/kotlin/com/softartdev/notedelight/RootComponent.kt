package com.softartdev.notedelight

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.softartdev.notedelight.di.getViewModel
import com.softartdev.notedelight.ui.*

class RootComponent(
    componentContext: ComponentContext,
) : NoteDelightRoot, ComponentContext by componentContext {

    private val navigation = StackNavigation<Configuration>()
    private val stack = childStack(
        source = navigation,
        initialConfiguration = Configuration.Splash,
        handleBackButton = true,
        childFactory = ::createChild
    )
    override val childStack: Value<ChildStack<*, ContentChild>> = stack

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
            navSignIn = { navigation.replaceCurrent(Configuration.SignIn) },
            navMain = { navigation.replaceCurrent(Configuration.Main) },
        )
    }

    private fun signIn(): ContentChild = {
        SignInScreen(
            signInViewModel = getViewModel(),
            navMain = { navigation.replaceCurrent(Configuration.Main) },
        )
    }

    private fun mainList(): ContentChild = {
        MainScreen(
            mainViewModel = getViewModel(),
            onItemClicked = { id -> navigation.push(Configuration.Details(itemId = id)) },
            onSettingsClick = { navigation.push(Configuration.Settings) },
        )
    }

    private fun noteDetail(configuration: Configuration.Details, context: ComponentContext): ContentChild = {
        val backWrapper = BackWrapper()
        val backCallback = BackCallback(isEnabled = true, onBack = backWrapper::invoke)
        backHandler.register(backCallback)
        context.lifecycle.doOnDestroy { backHandler.unregister(backCallback) }

        NoteDetail(
            noteViewModel = getViewModel(),
            noteId = configuration.itemId,
            backWrapper = backWrapper,
            navBack = navigation::pop
        )
    }

    private fun settings(): ContentChild = {
        SettingsScreen(
            onBackClick = navigation::pop,
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
