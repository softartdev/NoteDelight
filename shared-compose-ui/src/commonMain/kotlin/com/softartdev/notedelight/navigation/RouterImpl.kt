package com.softartdev.notedelight.navigation

import androidx.navigation.NavHostController
import com.softartdev.notedelight.shared.navigation.Router

class RouterImpl : Router {

    private lateinit var navController: NavHostController

    override fun setController(navController: Any) {
        this.navController = navController as NavHostController
    }

    override fun navigate(route: String) = navController.navigate(route)
}