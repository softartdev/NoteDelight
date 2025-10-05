package com.softartdev.notedelight

import com.softartdev.notedelight.navigation.Router


class UiThreadRouter(private val router: Router) : Router {

    override fun setController(navController: Any) = runOnUiThread {
        router.setController(navController)
    }

    override fun releaseController() = runOnUiThread { router.releaseController() }

    override fun <T : Any> navigate(route: T) = runOnUiThread { router.navigate(route) }

    override fun <T : Any> navigateClearingBackStack(route: T) = runOnUiThread {
        router.navigateClearingBackStack(route)
    }

    override fun <T : Any> popBackStack(route: T, inclusive: Boolean, saveState: Boolean): Boolean =
        runOnUiThread { router.popBackStack(route, inclusive, saveState) }

    override fun popBackStack(): Boolean = runOnUiThread { router.popBackStack() }
}