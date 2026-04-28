package com.softartdev.notedelight.interactor

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import co.touchlab.kermit.Logger
import java.lang.ref.WeakReference

/**
 * Tracks the currently `RESUMED` `FragmentActivity` for app-scoped components that need an
 * Activity host (e.g. `BiometricPrompt`). Holds the Activity in a `WeakReference` so a paused
 * Activity awaiting GC cannot keep its window alive through this provider.
 *
 * Registered as an [Application.ActivityLifecycleCallbacks] for the whole process at construction;
 * call [dispose] to unregister (mainly useful for tests — singletons normally live for the lifetime
 * of the process and the framework drops the registration when the process dies).
 */
internal class CurrentActivityProvider(
    private val application: Application
) : Application.ActivityLifecycleCallbacks {
    private val logger = Logger.withTag("CurrentActivityProvider")
    private var ref: WeakReference<FragmentActivity>? = null

    val current: FragmentActivity?
        get() = ref?.get()

    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    fun dispose() {
        application.unregisterActivityLifecycleCallbacks(this)
        ref?.clear()
        ref = null
    }

    override fun onActivityResumed(activity: Activity) {
        logger.i { "onActivityResumed: ${activity::class.java.simpleName}" }
        if (activity is FragmentActivity) ref = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        logger.i { "onActivityPaused: ${activity::class.java.simpleName}" }
        if (ref?.get() === activity) ref = null
    }

    override fun onActivityDestroyed(activity: Activity) {
        logger.i { "onActivityDestroyed: ${activity::class.java.simpleName}" }
        if (ref?.get() === activity) ref = null
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        logger.i { "onActivityCreated: ${activity::class.java.simpleName}" }
    }

    override fun onActivityStarted(activity: Activity) {
        logger.i { "onActivityStarted: ${activity::class.java.simpleName}" }
    }
    override fun onActivityStopped(activity: Activity) {
        logger.i { "onActivityStopped: ${activity::class.java.simpleName}" }
    }
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        logger.i { "onActivitySaveInstanceState: ${activity::class.java.simpleName}" }
    }
}
