package com.softartdev.notedelight.ui

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class DesktopNavTest : AbstractNavigationTest() {

    @get:Rule
    override val composeTestRule: ComposeContentTestRule = createComposeRule()

    @Test
    override fun routerLaunchSingleTopTest() = super.routerLaunchSingleTopTest()

    @Test
    override fun navControllerLaunchDoubleTopTest() = super.navControllerLaunchDoubleTopTest()
}