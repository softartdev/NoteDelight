package com.softartdev.notedelight.shared.base

import io.github.aakira.napier.Napier
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

abstract class BaseViewModel<T> : KmmViewModel() {

    open var initResult: T? = null
    abstract val loadingResult: T

    private val _resultStateFlow by lazy { MutableStateFlow(initResult ?: loadingResult) }
    val resultStateFlow: StateFlow<T> by lazy { _resultStateFlow.asStateFlow() }

    fun launch(
            useIdling: Boolean = true,
            block: suspend CoroutineScope.() -> T
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                if (useIdling) {
                    IdlingResource.increment()
                    loadingResult?.let { loading -> onResult(loading) }
                }
                onResult(block())
            } catch (e: Throwable) {
                Napier.e("❌", e)
                onResult(errorResult(e))
            } finally {
                if (useIdling) IdlingResource.decrement()
            }
        }.start()
    }

    fun launch(flow: Flow<T>) {
        viewModelScope.launch(Dispatchers.Default) {
            flow.onStart {
                loadingResult ?: return@onStart
                IdlingResource.increment()
                emit(loadingResult!!)
            }.onEach { result ->
                onResult(result)
                if (result == loadingResult) IdlingResource.decrement()
            }.catch { throwable ->
                Napier.e("❌", throwable)
                onResult(errorResult(throwable))
            }.launchIn(this)
        }.start()
    }

    private suspend inline fun onResult(result: T) = withContext(Dispatchers.Main) {
        _resultStateFlow.value = result
    }

    abstract fun errorResult(throwable: Throwable): T

//    @androidx.annotation.VisibleForTesting
    fun resetLoadingResult() {
        _resultStateFlow.value = loadingResult
    }
}