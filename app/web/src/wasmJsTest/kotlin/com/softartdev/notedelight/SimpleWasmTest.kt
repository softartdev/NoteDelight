@file:OptIn(ExperimentalTestApi::class, ExperimentalWasmJsInterop::class)

package com.softartdev.notedelight

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SimpleWasmTest {

    @Test
    fun simplePassTest() {
        assertTrue(true, "This test should pass")
    }

    @Test
    fun simpleAdditionTest() {
        assertEquals(4, 2 + 2, "Basic arithmetic should work")
    }

    @Test
    fun simpleComposeTest() = runComposeUiTest {
        // Declares a mock UI to demonstrate API calls
        // Replace with your own declarations to test the code of your project
        setContent {
            var text by remember { mutableStateOf("Hello") }
            Button(
                modifier = Modifier.testTag("button"),
                onClick = { text = "Compose" }
            ) {
                Text("Click me")
            }
            Text(
                modifier = Modifier.testTag("text"),
                text = text
            )
        }
        // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
        onNodeWithTag("text").assertTextEquals("Hello")
        onNodeWithTag("button").performClick()
        onNodeWithTag("text").assertTextEquals("Compose")
    }
}
