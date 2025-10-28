package com.softartdev.notedelight.navigation

interface Router {

    fun setController(navController: Any)

    fun releaseController()

    fun <T : Any> navigate(route: T)

    fun <T : Any> navigateClearingBackStack(route: T)

    fun <T : Any> popBackStack(route: T, inclusive: Boolean, saveState: Boolean): Boolean

    fun popBackStack(): Boolean

    fun setAdaptiveNavigator(adaptiveNavigator: Any)

    fun releaseAdaptiveNavigator()

    suspend fun adaptiveNavigateToDetail(contentKey: Long? = null)

    suspend fun adaptiveNavigateBack(): Boolean
}
