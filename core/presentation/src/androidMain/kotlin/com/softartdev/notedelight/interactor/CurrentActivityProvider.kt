package com.softartdev.notedelight.interactor

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import co.touchlab.kermit.Logger
import java.lang.ref.WeakReference

/**
 * Tracks the latest live `FragmentActivity` for app-scoped components that need an Activity host
 * (e.g. `BiometricPrompt`). The reference is set during creation/start/resume — the three early
 * lifecycle callbacks that are guaranteed to fire while the Activity is still live — and cleared on
 * destruction. We deliberately do **not** clear in `onActivityPaused`/`onActivityStopped` so that a
 * brief pause (overlay dialog, configuration change in flight) does not blank the host.
 *
 * The Activity is held weakly so a destroyed instance pending GC cannot keep its window leaked
 * through this provider. Registered as an [Application.ActivityLifecycleCallbacks] for the whole
 * process at construction; call [dispose] to unregister (mainly useful for tests — singletons
 * normally live for the lifetime of the process).
 */
internal class CurrentActivityProvider(
    private val application: Application,
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

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        logger.i { "onActivityCreated: ${activity::class.java.simpleName}" }
        captureIfFragmentActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        logger.i { "onActivityStarted: ${activity::class.java.simpleName}" }
        captureIfFragmentActivity(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        logger.i { "onActivityResumed: ${activity::class.java.simpleName}" }
        captureIfFragmentActivity(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        logger.i { "onActivityPaused: ${activity::class.java.simpleName}" }
        // Intentionally keep ref: a paused Activity can still host a BiometricPrompt that is about
        // to resume the same instance (e.g. a transient overlay).
    }

    override fun onActivityStopped(activity: Activity) {
        logger.i { "onActivityStopped: ${activity::class.java.simpleName}" }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        logger.i { "onActivitySaveInstanceState: ${activity::class.java.simpleName}" }
    }

    override fun onActivityDestroyed(activity: Activity) {
        logger.i { "onActivityDestroyed: ${activity::class.java.simpleName}" }
        if (ref?.get() === activity) ref = null
    }

    private fun captureIfFragmentActivity(activity: Activity) {
        if (activity is FragmentActivity) ref = WeakReference(activity)
    }
}
