package gg.aquatic.waves.util

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.future.asCompletableFuture

fun <T> Deferred<T>.thenAccept(consumer: (T) -> Unit) {
    asCompletableFuture().thenAccept(consumer)
}

fun <T> Deferred<T>.thenRun( runnable: () -> Unit) {
    asCompletableFuture().thenRun(runnable)
}