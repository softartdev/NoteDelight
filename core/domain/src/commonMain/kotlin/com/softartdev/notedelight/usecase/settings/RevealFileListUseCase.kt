package com.softartdev.notedelight.usecase.settings

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RevealFileListUseCase {
    private var tapCount: Int = 0
    private var resetJob: Job? = null

    fun onTap(scope: CoroutineScope, onReveal: () -> Unit) {
        if (++tapCount >= FILE_LIST_REQUIRED_TAPS) {
            reset()
            onReveal()
        } else {
            scheduleReset(scope)
        }
    }

    private fun scheduleReset(scope: CoroutineScope) {
        resetJob?.cancel()
        resetJob = scope.launch {
            delay(FILE_LIST_TAP_INTERVAL_MILLIS)
            tapCount = 0
        }
    }

    private fun reset() {
        tapCount = 0
        resetJob?.cancel()
        resetJob = null
    }

    private companion object Companion {
        private const val FILE_LIST_REQUIRED_TAPS: Int = 5
        private const val FILE_LIST_TAP_INTERVAL_MILLIS: Long = 1_500L
    }
}
