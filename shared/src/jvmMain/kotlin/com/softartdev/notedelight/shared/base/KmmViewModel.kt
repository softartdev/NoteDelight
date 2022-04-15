package com.softartdev.notedelight.shared.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

actual open class KmmViewModel actual constructor() {

    actual val viewModelScope: CoroutineScope = createViewModelScope()

    actual open fun onCleared() {
        viewModelScope.cancel()
    }
}