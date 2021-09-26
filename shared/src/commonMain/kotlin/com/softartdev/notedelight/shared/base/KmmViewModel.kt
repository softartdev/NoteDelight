package com.softartdev.notedelight.shared.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.native.concurrent.ThreadLocal

/*
Cannot use moko-mvvm library in this project, because it doesn't support jvm-target.
 */
expect open class KmmViewModel() {

    val viewModelScope: CoroutineScope

    open fun onCleared()
}

/**
 * Factory of viewModelScope. Copied from moko-mvvm library.
 */
@ThreadLocal
var createViewModelScope: () -> CoroutineScope = {
    CoroutineScope(context = Dispatchers.Main)
}
