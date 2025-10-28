package com.softartdev.notedelight

import com.softartdev.notedelight.navigation.Router
import kotlinx.coroutines.runBlocking


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

    override fun setAdaptiveNavigator(adaptiveNavigator: Any) = runOnUiThread {
        router.setAdaptiveNavigator(adaptiveNavigator)
    }

    override fun releaseAdaptiveNavigator() = runOnUiThread { router.releaseAdaptiveNavigator() }

    override suspend fun adaptiveNavigateToDetail(contentKey: Long?) = runOnUiThread {
        return@runOnUiThread runBlocking { router.adaptiveNavigateToDetail(contentKey) }
    }

    override suspend fun adaptiveNavigateBack(): Boolean = runOnUiThread {
        return@runOnUiThread runBlocking { router.adaptiveNavigateBack() }
    }
}