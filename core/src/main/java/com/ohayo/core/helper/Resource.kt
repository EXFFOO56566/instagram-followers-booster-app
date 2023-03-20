package com.ohayo.core.helper

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error<out T>(val throwable: Throwable, val data: T? = null) : Resource<T>()
}
