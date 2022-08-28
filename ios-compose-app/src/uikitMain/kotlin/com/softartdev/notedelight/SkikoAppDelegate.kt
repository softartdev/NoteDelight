package com.softartdev.notedelight

import androidx.compose.ui.window.Application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import com.softartdev.notedelight.shared.di.allModules
import com.softartdev.notedelight.ui.MainRootUI
import dev.icerock.moko.resources.desc.desc
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ObjCObjectBase
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import platform.UIKit.*

class SkikoAppDelegate : UIResponder, UIApplicationDelegateProtocol {
    companion object : UIResponderMeta(), UIApplicationDelegateProtocolMeta

    @ObjCObjectBase.OverrideInit
    constructor() : super()

    init {
        Napier.base(antilog = DebugAntilog())
        startKoin {
            printLogger(level = Level.DEBUG)
            modules(allModules)
        }
    }
    private val lifecycle = LifecycleRegistry()
    private val root = RootComponent(DefaultComponentContext(lifecycle))

    private var _window: UIWindow? = null
    override fun setWindow(window: UIWindow?) {
        _window = window
    }
    override fun window() = _window

    override fun application(
        application: UIApplication,
        didFinishLaunchingWithOptions: Map<Any?, *>?
    ): Boolean {
        window = UIWindow(frame = UIScreen.mainScreen.bounds)
        window!!.rootViewController = Application(
            title = MR.strings.app_name.desc().localized()
        ) {
            MainRootUI(root)
        }
        window!!.makeKeyAndVisible()
        return true
    }

    override fun applicationDidBecomeActive(application: UIApplication) = lifecycle.resume()
    override fun applicationWillResignActive(application: UIApplication) = lifecycle.stop()
    override fun applicationWillTerminate(application: UIApplication) = lifecycle.destroy()
}
