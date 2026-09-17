@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.ComposeContentTestRule

fun reflect(composeTestRule: ComposeContentTestRule): ComposeUiTest {
    if (composeTestRule is ComposeUiTest) return composeTestRule
    var c: Class<*>? = composeTestRule.javaClass
    while (c != null && c != Any::class.java) {
        try {
            val m = c.getDeclaredMethod("getComposeTest")
            m.isAccessible = true
            val v = m.invoke(composeTestRule)
            if (v is ComposeUiTest) return v
        } catch (_: Throwable) {
        }
        try {
            val f = c.getDeclaredField("composeTest")
            f.isAccessible = true
            val v = f.get(composeTestRule)
            if (v is ComposeUiTest) return v
        } catch (_: Throwable) {
        }
        try {
            val f = c.getDeclaredField("environment")
            f.isAccessible = true
            val env = f.get(composeTestRule)
            if (env != null) {
                if (env is ComposeUiTest) return env
                try {
                    val m = env.javaClass.getMethod("getTest")
                    val v = m.invoke(env)
                    if (v is ComposeUiTest) return v
                } catch (_: Throwable) {
                }
            }
        } catch (_: Throwable) {
        }
        for (m in c.declaredMethods) {
            if (m.parameterTypes.isEmpty() && ComposeUiTest::class.java.isAssignableFrom(m.returnType)) {
                try {
                    m.isAccessible = true
                    val v = m.invoke(composeTestRule)
                    if (v is ComposeUiTest) return v
                } catch (_: Throwable) {
                }
            }
        }
        for (f in c.declaredFields) {
            if (ComposeUiTest::class.java.isAssignableFrom(f.type)) {
                try {
                    f.isAccessible = true
                    val v = f.get(composeTestRule)
                    if (v is ComposeUiTest) return v
                } catch (_: Throwable) {
                }
            }
        }
        c = c.superclass
    }
    throw IllegalStateException("ComposeUiTest not found")
}
