package com.softartdev.notedelight

import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import com.softartdev.notedelight.shared.di.allModules
import com.softartdev.notedelight.ui.MainRootUI
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import platform.UIKit.UIViewController

class SkikoHelper {
    val lifecycle = LifecycleRegistry()
    val root = RootComponent(DefaultComponentContext(lifecycle))
    val applicationUIViewController: UIViewController = ComposeUIViewController { MainRootUI(root) }

    fun appInit() {
        Napier.base(antilog = DebugAntilog())
        startKoin {
            printLogger(level = Level.DEBUG)
            modules(allModules)
        }
    }

    fun resumeLifecycle() = lifecycle.resume()
    fun stopLifecycle() = lifecycle.stop()
    fun destroyLifecycle() = lifecycle.destroy()
}