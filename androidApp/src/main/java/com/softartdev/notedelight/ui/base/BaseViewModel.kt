package com.softartdev.notedelight.ui.base

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softartdev.notedelight.util.EspressoIdlingResource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber

abstract class BaseViewModel<T> : ViewModel() {

    open var initResult: T? = null
    abstract val loadingResult: T

    private val _resultStateFlow by lazy { MutableStateFlow(initResult ?: loadingResult) }
    val resultStateFlow: StateFlow<T> by lazy { _resultStateFlow.asStateFlow() }

    fun launch(
            useIdling: Boolean = true,
            block: suspend CoroutineScope.() -> T
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (useIdling) {
                    EspressoIdlingResource.increment()
                    loadingResult?.let { loading -> onResult(loading) }
                }
                onResult(block())
            } catch (e: Throwable) {
                Timber.e(e)
                onResult(errorResult(e))
            } finally {
                if (useIdling) EspressoIdlingResource.decrement()
            }
        }.start()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun launch(flow: Flow<T>) {
        viewModelScope.launch(Dispatchers.IO) {
            flow.onStart {
                loadingResult ?: return@onStart
                EspressoIdlingResource.increment()
                emit(loadingResult!!)
            }.onEach { result ->
                onResult(result)
                if (result == loadingResult) EspressoIdlingResource.decrement()
            }.catch { throwable ->
                Timber.e(throwable)
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
}