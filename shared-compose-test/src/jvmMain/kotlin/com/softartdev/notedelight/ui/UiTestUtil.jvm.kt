package com.softartdev.notedelight.ui

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.softartdev.notedelight.RootComponent
import com.softartdev.notedelight.shared.runOnUiThread

actual object UiTestUtil {
    actual val decomposeRootComponent: RootComponent
        get() {
            val lifecycle = LifecycleRegistry()
            val root = runOnUiThread {
                RootComponent(componentContext = DefaultComponentContext(lifecycle))
            }
            return root
        }
}
