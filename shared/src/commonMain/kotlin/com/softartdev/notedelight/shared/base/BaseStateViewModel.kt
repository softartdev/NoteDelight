package com.softartdev.notedelight.shared.base

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

abstract class BaseStateViewModel<T> : ViewModel() {

    open var initResult: T? = null
    abstract val loadingResult: T

    private val _resultStateFlow by lazy { MutableStateFlow(initResult ?: loadingResult) }
    val resultStateFlow: StateFlow<T> by lazy { _resultStateFlow.asStateFlow() }

    fun launch(useIdling: Boolean = true, block: suspend CoroutineScope.() -> T) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (useIdling) {
                    IdlingRes.increment()
                    loadingResult?.let { loading -> onResult(loading) }
                }
                onResult(block())
            } catch (e: Throwable) {
                Napier.e("❌", e)
                onResult(errorResult(e))
            } finally {
                if (useIdling) IdlingRes.decrement()
            }
        }.start()
    }

    fun launch(useIdling: Boolean = true, flow: Flow<T>) {
        viewModelScope.launch(Dispatchers.IO) {
            flow.onStart {
                loadingResult ?: return@onStart
                if (useIdling) IdlingRes.increment()
                emit(loadingResult!!)
            }.onEach { result ->
                onResult(result)
                if (useIdling && result == loadingResult) IdlingRes.decrement()
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

    @VisibleForTesting
    fun resetLoadingResult() {
        _resultStateFlow.value = loadingResult
    }

    public override fun onCleared() = super.onCleared()//FIXME
}