package com.softartdev.notedelight

import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.runOnUiThread

class UiThreadRouter(private val router: Router) : Router {

    override fun setController(navController: Any) = runOnUiThread {
        router.setController(navController)
    }

    override fun releaseController() = runOnUiThread { router.releaseController() }

    override fun navigate(route: String) = runOnUiThread { router.navigate(route) }

    override fun navigateClearingBackStack(route: String) = runOnUiThread {
        router.navigateClearingBackStack(route)
    }

    override fun popBackStack(route: String, inclusive: Boolean, saveState: Boolean): Boolean =
        runOnUiThread { router.popBackStack(route, inclusive, saveState) }

    override fun popBackStack(): Boolean = runOnUiThread { router.popBackStack() }
}