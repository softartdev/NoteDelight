@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.softartdev.notedelight.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.savedstate.SavedState
import co.touchlab.kermit.Logger

class RouterImpl : Router, NavController.OnDestinationChangedListener {
    private val logger = Logger.withTag(this@RouterImpl::class.simpleName.toString())
    private var navController: NavHostController? = null
    private var adaptiveNavigator: ThreePaneScaffoldNavigator<Long>? = null

    override fun setController(navController: Any) {
        logger.d { "Setting NavController" }
        this.navController = navController as NavHostController
        this.navController?.addOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: SavedState?) = logger.d {
        """
            Destination changed to: ${destination.route} with args: $arguments
            Current back stack entry route: ${controller.currentBackStackEntry?.destination?.route}
            Previous back stack entry route: ${controller.previousBackStackEntry?.destination?.route}
        """.trimIndent()
    }

    override fun releaseController() {
        logger.d { "Releasing NavController" }
        navController?.removeOnDestinationChangedListener(this)
        navController = null
    }

    override fun <T : Any> navigate(route: T) = navController?.navigate(route) {
        launchSingleTop = true
    } ?: logger.e { "navController is null while navigate to $route" }

    override fun <T : Any> navigateClearingBackStack(route: T) = navController?.let { navHostController ->
        var popped = true
        while (popped) {
            popped = navHostController.popBackStack()
        }
        navHostController.navigate(route)
    } ?: logger.e { "navController is null while navigateClearingBackStack to $route" }

    override fun <T : Any> popBackStack(route: T, inclusive: Boolean, saveState: Boolean): Boolean =
        navController?.popBackStack(route, inclusive, saveState)
            ?: logger.e { "navController is null while popBackStack to $route" }.let { false }

    override fun popBackStack(): Boolean = navController?.popBackStack()
        ?: logger.e { "navController is null while popBackStack" }.let { false }

    @Suppress("UNCHECKED_CAST")
    override fun setAdaptiveNavigator(adaptiveNavigator: Any) {
        logger.d { "Setting AdaptiveNavigator" }
        this.adaptiveNavigator = adaptiveNavigator as ThreePaneScaffoldNavigator<Long>
    }

    override fun releaseAdaptiveNavigator(adaptiveNavigator: Any?) = when (this.adaptiveNavigator) {
        null -> logger.d { "Releasing AdaptiveNavigator: navigator already released" }
        adaptiveNavigator -> {
            logger.d { "Releasing AdaptiveNavigator" }
            this.adaptiveNavigator = null
        }
        else -> logger.d { "Skip releasing AdaptiveNavigator: another navigator is active" }
    }

    override suspend fun adaptiveNavigateToDetail(contentKey: Long?) =
        adaptiveNavigator?.navigateTo(ListDetailPaneScaffoldRole.Detail, contentKey)
            ?: logger.e { "adaptiveNavigator is null while adaptiveNavigateToDetail, contentKey = $contentKey" }

    override suspend fun adaptiveNavigateBack(): Boolean = adaptiveNavigator?.navigateBack()
        ?: logger.e { "adaptiveNavigator is null while adaptiveNavigateBack" }.let { false }
}
