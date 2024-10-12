package com.softartdev.notedelight.shared.navigation

interface Router {

    fun setController(navController: Any)

    fun releaseController()

    fun navigate(route: String)

    fun navigateClearingBackStack(route: String)

    fun popBackStack(route: String, inclusive: Boolean, saveState: Boolean): Boolean

    fun popBackStack(): Boolean
}
