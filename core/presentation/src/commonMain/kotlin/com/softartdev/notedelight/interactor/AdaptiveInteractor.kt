package com.softartdev.notedelight.interactor

import kotlinx.coroutines.flow.MutableStateFlow

class AdaptiveInteractor {
    val selectedNoteIdStateFlow: MutableStateFlow<Long?> = MutableStateFlow(null)
}