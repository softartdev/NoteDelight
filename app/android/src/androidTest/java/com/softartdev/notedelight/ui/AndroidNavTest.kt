package com.softartdev.notedelight.ui

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import com.softartdev.notedelight.di.sharedModules
import com.softartdev.notedelight.di.uiModules
import org.junit.Rule
import org.junit.Test
import org.koin.core.module.Module

class AndroidNavTest : AbstractNavigationTest() {

    @get:Rule
    override val composeTestRule: ComposeContentTestRule = createComposeRule()

    override val koinModules: List<Module> = sharedModules + uiModules

    @Test
    override fun routerLaunchSingleTopTest() = super.routerLaunchSingleTopTest()

    @Test
    override fun navControllerLaunchDoubleTopTest() = super.navControllerLaunchDoubleTopTest()
}