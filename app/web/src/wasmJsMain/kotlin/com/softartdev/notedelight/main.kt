@file:OptIn(ExperimentalComposeUiApi::class)

package com.softartdev.notedelight

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.di.sharedModules
import com.softartdev.notedelight.di.uiModules
import com.softartdev.notedelight.util.DEFAULT_APP_LOG_TAG
import com.softartdev.notedelight.util.kermitLogger
import kotlinx.browser.document
import org.koin.core.context.startKoin

fun main() {
    Logger.setTag(DEFAULT_APP_LOG_TAG)
    startKoin {
        kermitLogger()
        modules(sharedModules + uiModules)
    }
    ComposeViewport(document.body!!) {
        App()
    }
}
