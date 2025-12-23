@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.ComposeContentTestRule

fun reflect(composeTestRule: ComposeContentTestRule): ComposeUiTest {
    var c: Class<*>? = composeTestRule.javaClass
    while (c != null && c != Any::class.java) {
        try {
            val f = c.getDeclaredField("composeTest")
            f.isAccessible = true
            val v = f.get(composeTestRule)
            if (v is ComposeUiTest) return v
        } catch (_: NoSuchFieldException) {
        } catch (_: IllegalAccessException) {
        }
        c = c.superclass
    }
    throw IllegalStateException("ComposeUiTest not found")
}
