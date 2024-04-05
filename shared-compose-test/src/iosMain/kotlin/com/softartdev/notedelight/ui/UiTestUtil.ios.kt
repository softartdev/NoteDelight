package com.softartdev.notedelight.ui

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.softartdev.notedelight.RootComponent

actual object UiTestUtil {
    actual val decomposeRootComponent: RootComponent
        get() {
            val lifecycle = LifecycleRegistry()
            return RootComponent(DefaultComponentContext(lifecycle))
        }
}