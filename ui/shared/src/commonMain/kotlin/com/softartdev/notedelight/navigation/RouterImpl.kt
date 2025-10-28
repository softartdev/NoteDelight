@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.softartdev.notedelight.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.navigation.NavHostController

class RouterImpl : Router {
    private var navController: NavHostController? = null
    private var adaptiveNavigator: ThreePaneScaffoldNavigator<Long>? = null

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