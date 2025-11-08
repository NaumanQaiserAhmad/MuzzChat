package com.muzz.core.domain.util

sealed class AppResult<out T> {
    data class Success<T>(val data: T): AppResult<T>()
    data class Error(val throwable: Throwable): AppResult<Nothing>()

    inline fun <R> map(transform: (T) -> R): AppResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
    }
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }
}
