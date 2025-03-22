package com.softartdev.notedelight.navigation

import androidx.navigation.NavHostController

class RouterImpl : Router {

    private var navController: NavHostController? = null

    override fun setController(navController: Any) {
        this.navController = navController as NavHostController
    }

    override fun releaseController() {
        navController = null
    }

    override fun <T : Any> navigate(route: T) = navController!!.navigate(route)

    override fun <T : Any> navigateClearingBackStack(route: T) {
        var popped = true
        while (popped) {
            popped = navController!!.popBackStack()
        }
        navController!!.navigate(route)
    }

    override fun <T : Any> popBackStack(route: T, inclusive: Boolean, saveState: Boolean): Boolean =
        navController!!.popBackStack(route, inclusive, saveState)

    override fun popBackStack() = navController!!.popBackStack()
}