package com.softartdev.notedelight

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ExampleTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun myTest(){
        rule.setContent {
            var text by remember { mutableStateOf("Hello") }
            Text(
                modifier = Modifier.testTag("text"),
                text = text,
            )
            Button(
                modifier = Modifier.testTag("button"),
                onClick = { text = "Compose" },
            ){
                Text("Click me")
            }
        }
//        rule.waitUntil(timeoutMillis = 7_000) { false }
        rule.onNodeWithTag("text").assertTextEquals("Hello")
        rule.onNodeWithTag("button").performClick()
        rule.onNodeWithTag("text").assertTextEquals("Compose")
    }
}
