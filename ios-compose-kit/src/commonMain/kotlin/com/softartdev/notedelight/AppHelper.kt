package com.softartdev.notedelight

import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.create
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.pause
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.start
import com.arkivanov.essenty.lifecycle.stop
import com.softartdev.notedelight.shared.di.allModules
import com.softartdev.notedelight.shared.util.NapierKoinLogger
import com.softartdev.notedelight.ui.MainRootUI
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import platform.UIKit.UIViewController

class AppHelper {
    val lifecycle = LifecycleRegistry()
    val root = RootComponent(DefaultComponentContext(lifecycle))
    val appUIViewController: UIViewController = ComposeUIViewController { MainRootUI(root) }

    fun appInit() {
        Napier.base(antilog = DebugAntilog())
        startKoin {
            logger(NapierKoinLogger(Level.DEBUG))
            modules(allModules)
        }
    }

    fun createLifecycle() = lifecycle.create()
    fun startLifecycle() = lifecycle.start()
    fun resumeLifecycle() = lifecycle.resume()
    fun pauseLifecycle() = lifecycle.pause()
    fun stopLifecycle() = lifecycle.stop()
    fun destroyLifecycle() = lifecycle.destroy()
}