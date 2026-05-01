package com.softartdev.notedelight.interactor

interface AutofillInteractor {
    fun attach(autofillManager: Any)
    fun commit()
    fun cancel()
    fun detach()
}
