package com.softartdev.notedelight

import platform.UIKit.*

class SkikoAppDelegate : UIResponder, UIApplicationDelegateProtocol {
    companion object Companion : UIResponderMeta(), UIApplicationDelegateProtocolMeta

    @OverrideInit
    constructor() : super()

    val skikoHelper = SkikoHelper()

    private var _window: UIWindow? = null

    init {
        skikoHelper.appInit()
    }

    override fun window() = _window
    override fun setWindow(window: UIWindow?) {
        _window = window
    }

    override fun application(
        application: UIApplication,
        didFinishLaunchingWithOptions: Map<Any?, *>?
    ): Boolean {
        window = UIWindow(frame = UIScreen.mainScreen.bounds)
        window!!.rootViewController = skikoHelper.applicationUIViewController
        window!!.makeKeyAndVisible()
        return true
    }

    override fun applicationDidBecomeActive(application: UIApplication) {
        skikoHelper.resumeLifecycle()
    }

    override fun applicationWillResignActive(application: UIApplication) {
        skikoHelper.stopLifecycle()
    }

    override fun applicationWillTerminate(application: UIApplication) {
        skikoHelper.destroyLifecycle()
    }
}