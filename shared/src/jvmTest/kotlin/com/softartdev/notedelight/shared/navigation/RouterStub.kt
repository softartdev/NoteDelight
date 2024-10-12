package com.softartdev.notedelight.shared.navigation

import io.github.aakira.napier.Napier

class RouterStub : Router {

    override fun setController(navController: Any) = Napier.d("setController: $navController")

    override fun releaseController() = Napier.d(message = "releaseController")

    override fun navigate(route: String) = Napier.d(message = "navigate: $route")

    override fun navigateClearingBackStack(route: String) =
        Napier.d("navigateClearingBackStack: $route")

    override fun popBackStack(route: String, inclusive: Boolean, saveState: Boolean): Boolean {
        Napier.d("popBackStack: $route, inclusive: $inclusive, saveState: $saveState")
        return true
    }

    override fun popBackStack(): Boolean {
        Napier.d(message = "popBackStack")
        return true
    }
}