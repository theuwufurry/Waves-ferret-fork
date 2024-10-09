package gg.aquatic.waves.util

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun <T> Deferred<T>.thenAccept(consumer: (T) -> Unit) {
    runBlocking {
        launch {
            consumer(this@thenAccept.await())
        }
    }
}

fun <T> Deferred<T>.thenRun( runnable: () -> Unit) {
    runBlocking {
        launch {
            this@thenRun.join()
            runnable()
        }
    }
}