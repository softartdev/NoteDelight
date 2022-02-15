package com.softartdev.notedelight.compose

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.core.app.ActivityScenario
import org.junit.rules.ExternalResource

inline fun <reified A : ComponentActivity> customAndroidComposeRule(
    noinline beforeActivityLaunched: () -> Unit
): AndroidComposeTestRule<CustomActivityScenarioRule<A>, A> {
    return customAndroidComposeRule(A::class.java, beforeActivityLaunched)
}

fun <A : ComponentActivity> customAndroidComposeRule(
    activityClass: Class<A>,
    beforeActivityLaunched: () -> Unit
): AndroidComposeTestRule<CustomActivityScenarioRule<A>, A> = AndroidComposeTestRule(
    activityRule = CustomActivityScenarioRule(activityClass, beforeActivityLaunched),
    activityProvider = ::provideActivity
)

fun <A : Activity> provideActivity(customActivityScenarioRule: CustomActivityScenarioRule<A>): A {
    var activity: A? = null
    customActivityScenarioRule.scenario.onActivity { currentActivity ->
        activity = currentActivity
    }
    return requireNotNull(activity) { "Activity was not set in the CustomActivityScenarioRule!" }
}

class CustomActivityScenarioRule<A : Activity>(
    private val activityClass: Class<A>,
    private val beforeActivityLaunched: () -> Unit
) : ExternalResource() {

    internal lateinit var scenario: ActivityScenario<A>

    override fun before() {
        beforeActivityLaunched()
        scenario = ActivityScenario.launch(activityClass)
    }

    override fun after() = scenario.close()
}