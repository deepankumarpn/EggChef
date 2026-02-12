package deepankumarpn.github.io.eggchef.base

sealed class StateFullResult<out T> {
    data object Loading : StateFullResult<Nothing>()
    data class Success<T>(val data: T) : StateFullResult<T>()
    data class Error(val exception: Throwable) : StateFullResult<Nothing>()
}
