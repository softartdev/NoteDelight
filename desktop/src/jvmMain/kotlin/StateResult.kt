import androidx.compose.runtime.*
import kotlinx.coroutines.launch

sealed class StateResult<out R> {
    data class Success<out T>(val data: T) : StateResult<T>()
    data class Error(val exception: Exception) : StateResult<Nothing>()
}

typealias RepositoryCall<T> = suspend ((StateResult<T>) -> Unit) -> Unit

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val exception: Exception) : UiState<Nothing>()
}

@Composable
fun <T> uiStateFrom(
    vararg inputs: Any?,
    repositoryCall: RepositoryCall<T>
): MutableState<UiState<T>> {
    val state: MutableState<UiState<T>> = remember { mutableStateOf(UiState.Loading) }
    val scope = rememberCoroutineScope()

    DisposableEffect(*inputs) {
        state.value = UiState.Loading
        scope.launch {
            repositoryCall { result ->
                state.value = when (result) {
                    is StateResult.Success -> UiState.Success(result.data)
                    is StateResult.Error -> UiState.Error(result.exception)
                }
            }
        }
        onDispose {  }
    }

    return state
}