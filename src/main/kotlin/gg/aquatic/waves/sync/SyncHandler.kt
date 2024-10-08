package gg.aquatic.waves.sync

import com.google.gson.Gson

object SyncHandler {

    inline fun <reified T> cacheCustom(value: T, namespace: String) {
        val json = Gson().toJson(value)
        // API -> cache the server data (namespace to json)
    }

}