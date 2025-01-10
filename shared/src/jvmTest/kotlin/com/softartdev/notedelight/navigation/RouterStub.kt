package com.softartdev.notedelight.navigation

import io.github.aakira.napier.Napier

class RouterStub : Router {

    override fun setController(navController: Any) = Napier.d("setController: $navController")

    override fun releaseController() = Napier.d(message = "releaseController")

    override fun <T : Any> navigate(route: T) = Napier.d(message = "navigate: $route")

    override fun <T : Any> navigateClearingBackStack(route: T) =
        Napier.d("navigateClearingBackStack: $route")

    override fun <T : Any> popBackStack(route: T, inclusive: Boolean, saveState: Boolean): Boolean {
        Napier.d("popBackStack: $route, inclusive: $inclusive, saveState: $saveState")
        return true
    }

    override fun popBackStack(): Boolean {
        Napier.d(message = "popBackStack")
        return true
    }
}