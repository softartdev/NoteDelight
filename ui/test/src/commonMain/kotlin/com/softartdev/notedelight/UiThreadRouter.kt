package com.softartdev.notedelight

import com.softartdev.notedelight.navigation.Router


class UiThreadRouter(private val router: Router) : Router {

    override fun setController(navController: Any) = router.setController(navController)

    override fun releaseController() = router.releaseController()

    override fun <T : Any> navigate(route: T) = runOnUiThread { router.navigate(route) }

    override fun <T : Any> navigateClearingBackStack(route: T) =
        runOnUiThread { router.navigateClearingBackStack(route) }

    override fun <T : Any> popBackStack(route: T, inclusive: Boolean, saveState: Boolean): Boolean =
        runOnUiThread { router.popBackStack(route, inclusive, saveState) }

    override fun popBackStack(): Boolean = runOnUiThread { router.popBackStack() }

    override fun setAdaptiveNavigator(adaptiveNavigator: Any) =
        router.setAdaptiveNavigator(adaptiveNavigator)

    override fun releaseAdaptiveNavigator() = router.releaseAdaptiveNavigator()

    override suspend fun adaptiveNavigateToDetail(contentKey: Long?) =
        router.adaptiveNavigateToDetail(contentKey)

    override suspend fun adaptiveNavigateBack(): Boolean = router.adaptiveNavigateBack()
}