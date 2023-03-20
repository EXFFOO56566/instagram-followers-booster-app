package com.ohayo.core.extension

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.cancellation.CancellationException

@ExperimentalCoroutinesApi
fun <E> SendChannel<E>.safeOffer(value: E) = !isClosedForSend && try {
    trySend(value).isSuccess
} catch (e: CancellationException) {
    false
}

fun <T> Flow<T?>.mapToVoid(block: ((T?) -> Unit)? = null): Flow<Unit> {
    return this.map {
        block?.invoke(it)
        Unit
    }
}

fun <T> Flow<T>.debug(message: String): Flow<T> {
    return this.onEach {
        Timber.d("$message - $it")
    }
}

fun MutableSharedFlow<Unit>.call() {
    tryEmit(Unit)
}

fun <T> Flow<T>.bindTo(to: MutableStateFlow<T>): Flow<T> {
    return this.onEach(to::emit)
}

fun <T> Flow<T>.bindTo(to: MutableSharedFlow<T>): Flow<T> {
    return this.onEach(to::emit)
}

fun <A, B : Any, R> Flow<A>.withLatestFrom(other: Flow<B>, transform: suspend (A, B) -> R): Flow<R> = flow {
    coroutineScope {
        val latestB = AtomicReference<B?>()
        val outerScope = this
        launch {
            try {
                other.collect { latestB.set(it) }
            } catch (e: CancellationException) {
                outerScope.cancel(e) // cancel outer scope on cancellation exception, too
            }
        }
        collect { a: A ->
            latestB.get()?.let { b -> emit(transform(a, b)) }
        }
    }
}