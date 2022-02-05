package com.softartdev.notedelight.compose

import android.app.Activity
import androidx.test.core.app.ActivityScenario
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.internal.util.Checks
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
    customActivityScenarioRule.getScenario().onActivity { currentActivity ->
        activity = currentActivity
    }
    if (activity == null) {
        throw IllegalStateException("Activity was not set in the ActivityScenarioRule!")
    }
    return activity!!
}

class CustomActivityScenarioRule<A : Activity>(
    activityClass: Class<A>,
    private val beforeActivityLaunched: () -> Unit
) : ExternalResource() {

    internal interface Supplier<T> {
        fun get(): T
    }

    private val scenarioSupplier: Supplier<ActivityScenario<A>>
    private var scenario: ActivityScenario<A>? = null

    init {
        scenarioSupplier = object : Supplier<ActivityScenario<A>> {
            override fun get(): ActivityScenario<A> {
                return ActivityScenario.launch(
                    Checks.checkNotNull(activityClass)
                )
            }
        }
    }

    @Throws(Throwable::class)
    override fun before() {
        beforeActivityLaunched()
        scenario = scenarioSupplier.get()
    }

    override fun after() = getScenario().close()

    fun getScenario(): ActivityScenario<A> = requireNotNull(scenario)
}