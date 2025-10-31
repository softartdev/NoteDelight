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

fun main() {
    Napier.base(antilog = DebugAntilog())
    startKoin {
        logger(NapierKoinLogger(Level.DEBUG))
        modules(sharedModules + uiModules)
    }
    ComposeViewport(document.body!!) {
        App()
    }
}