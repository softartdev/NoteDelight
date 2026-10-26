package com.softartdev.notedelight.interactor

class ShareInteractorStub : ShareInteractor() {
    var lastSharedText: String? = null

    override fun shareText(text: String) {
        lastSharedText = text
    }

    fun reset() {
        sharedTextStateFlow.value = null
        lastSharedText = null
    }
}
