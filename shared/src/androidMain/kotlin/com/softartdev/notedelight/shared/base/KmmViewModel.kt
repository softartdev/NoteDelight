package com.softartdev.notedelight.shared.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope as extViewModelScope
import kotlinx.coroutines.CoroutineScope

actual open class KmmViewModel actual constructor() : ViewModel() {

    actual val viewModelScope: CoroutineScope
        get() = extViewModelScope

    public actual override fun onCleared() = super.onCleared()
}