package com.softartdev.notedelight.interactor

import kotlinx.coroutines.flow.MutableStateFlow

abstract class ShareInteractor {
    val sharedTextStateFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    abstract fun shareText(text: String)
}
