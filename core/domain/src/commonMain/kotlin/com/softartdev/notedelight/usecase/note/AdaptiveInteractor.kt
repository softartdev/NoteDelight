package com.softartdev.notedelight.usecase.note

import kotlinx.coroutines.flow.MutableStateFlow

class AdaptiveInteractor {
    val selectedNoteIdStateFlow: MutableStateFlow<Long?> = MutableStateFlow(null)
}