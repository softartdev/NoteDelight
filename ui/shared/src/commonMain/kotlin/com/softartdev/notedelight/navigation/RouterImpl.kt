@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.softartdev.notedelight.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.savedstate.SavedState
import io.github.aakira.napier.Napier

class RouterImpl : Router, NavController.OnDestinationChangedListener {
    private var navController: NavHostController? = null
    private var adaptiveNavigator: ThreePaneScaffoldNavigator<Long>? = null

    override fun setController(navController: Any) {
        Napier.d("Setting NavController")
        this.navController = navController as NavHostController
        this.navController?.addOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: SavedState?) = Napier.d(
        message = """
            Destination changed to: ${destination.route} with args: $arguments
            Current back stack entry route: ${controller.currentBackStackEntry?.destination?.route}
            Previous back stack entry route: ${controller.previousBackStackEntry?.destination?.route}
        """.trimIndent()
    )

    override fun releaseController() {
        Napier.d("Releasing NavController")
        navController?.removeOnDestinationChangedListener(this)
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

    override fun setAdaptiveNavigator(adaptiveNavigator: Any) {
        this.adaptiveNavigator = adaptiveNavigator as ThreePaneScaffoldNavigator<Long>
    }

    override fun releaseAdaptiveNavigator() {
        adaptiveNavigator = null
    }

    override suspend fun adaptiveNavigateToDetail(contentKey: Long?) {
        adaptiveNavigator!!.navigateTo(ListDetailPaneScaffoldRole.Detail, contentKey)
    }

    override suspend fun adaptiveNavigateBack(): Boolean = adaptiveNavigator!!.navigateBack()
}