@file:OptIn(ExperimentalComposeUiApi::class)

package com.softartdev.notedelight

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.softartdev.notedelight.di.sharedModules
import com.softartdev.notedelight.di.uiModules
import com.softartdev.notedelight.util.NapierKoinLogger
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.browser.document
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

// Top-level property to check cross-origin isolation (required for Wasm js() calls)
private val isCrossOriginIsolated: Boolean = js("window.crossOriginIsolated").unsafeCast<Boolean?>() ?: false

fun main() {
    Napier.base(antilog = DebugAntilog())
    
    // Verify cross-origin isolation for OPFS support
    Napier.d("Cross-origin isolated: $isCrossOriginIsolated")
    if (!isCrossOriginIsolated) {
        Napier.w("⚠️ OPFS not available: cross-origin isolation not enabled. Database may not persist across page reloads.")
        Napier.w("⚠️ Ensure coi-serviceworker.js is registered and active.")
    } else {
        Napier.d("✅ Cross-origin isolation enabled - OPFS should be available")
    }
    
    startKoin {
        logger(NapierKoinLogger(Level.DEBUG))
        modules(sharedModules + uiModules)
    }
    ComposeViewport(document.body!!) {
        App()
    }
}