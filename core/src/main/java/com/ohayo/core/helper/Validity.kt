package com.ohayo.core.helper

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

sealed class Validity<out T> {
    data class Valid<T>(var value: T) : Validity<T>()
    object Invalid : Validity<Nothing>()
    object Initialized : Validity<Nothing>()

    fun validValue(): T? = (this as? Valid)?.value
}

fun <T> Flow<Validity<T>>.filterValid(): Flow<T> =
    filter { it is Validity.Valid }.map { (it as Validity.Valid).value }