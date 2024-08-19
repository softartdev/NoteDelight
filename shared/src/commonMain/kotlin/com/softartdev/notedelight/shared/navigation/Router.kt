package com.softartdev.notedelight.shared.navigation

interface Router {

    fun setController(navController: Any)

    fun navigate(route: String)

    fun navigateClearingBackStack(route: String)

    fun popBackStack(): Boolean
}
