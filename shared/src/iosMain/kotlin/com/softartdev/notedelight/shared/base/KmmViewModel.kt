package com.softartdev.notedelight.shared.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.native.internal.GC

actual open class KmmViewModel actual constructor() {

    actual val viewModelScope: CoroutineScope = MainScope()

    actual open fun onCleared() {
        viewModelScope.cancel()

        dispatch_async(dispatch_get_main_queue()) { GC.collect() }
    }
}