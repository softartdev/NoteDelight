package com.softartdev.notedelight.util

import co.touchlab.kermit.Logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * An object that determines idleness by maintaining an internal counter.
 * When the counter is 0 - it is considered to be idle, when it is non-zero it is not idle.
 * This is similar to the way a [kotlinx.coroutines.sync.Semaphore] behaves.
 *
 * The counter may be incremented or decremented from any coroutine. Thread-safe operations
 * are ensured using a [Mutex]. If the counter reaches an illogical state (like counter less than zero),
 * an error is logged but no exception is thrown to prevent test failures.
 *
 * This object is used to wrap up operations that while in progress should block UI tests
 * from accessing the UI. It integrates with [com.softartdev.notedelight.util.ComposeCountingIdlingResource]
 * for Compose UI testing.
 *
 * ## Usage in ViewModels
 *
 * Wrap async operations in ViewModels with increment/decrement calls:
 *
 * ```kotlin
 * private fun loadData() = viewModelScope.launch {
 *     CountingIdlingRes.increment()
 *     try {
 *         mutableStateFlow.update(Result::showLoading)
 *         val data = withContext(Dispatchers.IO) {
 *             repository.loadData()
 *         }
 *         mutableStateFlow.update { Result.Success(data) }
 *     } catch (e: Throwable) {
 *         handleError(e)
 *     } finally {
 *         mutableStateFlow.update(Result::hideLoading)
 *         CountingIdlingRes.decrement()
 *     }
 * }
 * ```
 *
 * ## Integration with UI Tests
 *
 * The [ComposeCountingIdlingResource] wraps this object and implements [androidx.compose.ui.test.IdlingResource]
 * for Compose UI tests. Tests automatically wait for the counter to reach zero before proceeding.
 *
 * ```kotlin
 * // In test setup
 * composeTestRule.registerIdlingResource(ComposeCountingIdlingResource)
 * ```
 *
 * @see [ComposeCountingIdlingResource] for Compose UI test integration
 */
object CountingIdlingRes {
    private val logger = Logger.withTag("CountingIdlingRes")
    private val mutex = Mutex()

    var counter: Int = 0
        private set

    val isIdleNow: Boolean
        get() = counter == 0
    
    suspend fun increment() = mutex.withLock(action = ::unsafeIncrement)
    
    suspend fun decrement() = mutex.withLock(action = ::unsafeDecrement)

    private fun unsafeIncrement() {
        counter++
        logger.d { "IdlingResource incremented, counter = $counter" }
    }
    
    private fun unsafeDecrement() {
        if (counter <= 0) {
            logger.e { "IdlingResource counter has been corrupted! Counter = $counter" }
            return
        }
        counter--
        logger.d { "IdlingResource decremented, counter = $counter" }
    }
}