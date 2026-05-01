package com.softartdev.notedelight.navigation

import co.touchlab.kermit.Logger

class RouterStub : Router {
    private val logger = Logger.withTag(this@RouterStub::class.simpleName.toString())

    override fun setController(navController: Any) = logger.d { "setController: $navController" }

    override fun releaseController() = logger.d { "releaseController" }

    override fun <T : Any> navigate(route: T) = logger.d { "navigate: $route" }

    override fun <T : Any> navigateClearingBackStack(route: T) =
        logger.d { "navigateClearingBackStack: $route" }

    override fun <T : Any> popBackStack(route: T, inclusive: Boolean, saveState: Boolean): Boolean {
        logger.d { "popBackStack: $route, inclusive: $inclusive, saveState: $saveState" }
        return true
    }

    override fun popBackStack(): Boolean {
        logger.d { "popBackStack" }
        return true
    }

    override fun setAdaptiveNavigator(adaptiveNavigator: Any) =
        logger.d { "setAdaptiveNavigator: $adaptiveNavigator" }

    override fun releaseAdaptiveNavigator(adaptiveNavigator: Any?) =
        logger.d { "releaseAdaptiveNavigator" }

    override suspend fun adaptiveNavigateToDetail(contentKey: Long?) = 
        logger.d { "adaptiveNavigateToDetail: $contentKey" }

    override suspend fun adaptiveNavigateBack(): Boolean {
        logger.d { "adaptiveNavigateBack" }
        return true
    }
}
