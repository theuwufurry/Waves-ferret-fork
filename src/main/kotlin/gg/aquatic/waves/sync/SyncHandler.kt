package gg.aquatic.waves.sync

import com.google.gson.Gson
import com.google.gson.JsonObject
import gg.aquatic.waves.sync.packet.PacketResponse
import gg.aquatic.waves.sync.packet.SyncPacket
import kotlinx.coroutines.*
import java.util.concurrent.CompletableFuture

object SyncHandler {

    lateinit var client: SyncClient

    val packetRegistry = mutableMapOf<String, SyncPacketHandler<*>>()

    init {
        registerPacket("waves_packet_response",PacketResponse.Handler)
    }

    fun registerPacket(id: String, handler: SyncPacketHandler<*>) {
        packetRegistry[id] = handler
    }

    suspend fun handlePacket(json: JsonObject): String? {
        val packetType = json.get("packetType").asString
        val handler = packetRegistry[packetType] ?: return null
        return handler.serializeAndHandle(json.asString)
    }

    suspend inline fun <reified T> cacheCustom(value: T, namespace: String) = coroutineScope {
        launch {
            val json = Gson().toJson(value)
            client.cacheCustom(json, namespace)
        }
    }

    suspend inline fun <reified T> getCustomCache(namespace: String): T? {
        val value = withContext(Dispatchers.IO) {
            return@withContext client.getCustomCache(namespace)
        }
        if (value.isEmpty() || value == "null") {
            return null
        }
        return Gson().fromJson(value, T::class.java)
    }

    suspend fun sendPacket(packet: SyncPacket, target: String) {
        sendPacket(packet, listOf(target))
    }

    suspend fun sendPacket(packet: SyncPacket, target: List<String>) {
        sendPacket(packet, target, broadcast = false, await = false)
    }

    private suspend fun sendPacket(packet: SyncPacket, target: List<String>, broadcast: Boolean, await: Boolean): String? {
        return client.sendPacket(packet, target, broadcast, await)
    }

    suspend fun broadcastPacket(packet: SyncPacket) {
        sendPacket(packet, listOf(), true, await = false)
    }

    suspend fun sendPacketAndAwait(packet: SyncPacket, target: String): String? {
        return sendPacket(packet, listOf(target), broadcast = false, await = true)
    }
}