package gg.aquatic.waves.sync

import com.google.gson.Gson
import java.util.concurrent.CompletableFuture

object SyncHandler {

    lateinit var client: SyncClient

    inline fun <reified T> cacheCustom(value: T, namespace: String): CompletableFuture<Void> {
        val json = Gson().toJson(value)
        val future = CompletableFuture<Void>()
        CompletableFuture.runAsync {
            client.cacheCustom(json, namespace).thenAccept {
                future.complete(null)
            }
        }
        return future
        // API -> cache the server data (namespace to json)
    }

    inline fun <reified T> getCustomCache(namespace: String): CompletableFuture<T?> {
        val future = CompletableFuture<T?>()

        CompletableFuture.runAsync {
            client.getCustomCache(namespace).thenAccept {
                if (it == null) {
                    future.complete(null)
                    return@thenAccept
                }
                future.complete(Gson().fromJson(it, T::class.java))
            }
        }
        return future
    }
}