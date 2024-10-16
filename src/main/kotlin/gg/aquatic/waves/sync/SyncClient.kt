package gg.aquatic.waves.sync

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import gg.aquatic.aquaticseries.lib.util.runSync
import gg.aquatic.waves.sync.packet.PacketResponse
import gg.aquatic.waves.sync.packet.SyncPacket
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import io.ktor.websocket.readText
import kotlinx.coroutines.*
import org.bukkit.Bukkit
import java.util.ArrayList
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.time.measureTime

class SyncClient(
    val ip: String,
    val port: Int,
    val protectionKey: String,
    val serverId: String,
) {

    val client = HttpClient(OkHttp) {
        engine {
            config {
                followRedirects(true)
            }
        }
        install(WebSockets) {
        }
        install(Auth) {
            digest {
                credentials {
                    DigestAuthCredentials(username = "admin", password = protectionKey)
                }
                realm = "Access to the '/' path"
            }
        }
    }

    val awaiting = HashMap<UUID, Pair<CompletableDeferred<String>,Long>>()

    private var socketConnection: DefaultWebSocketSession? = null

    suspend fun cacheCustom(data: String, namespace: String) = coroutineScope {
        launch {
            client.request("$ip:$port/cache/$namespace") {
                method = HttpMethod.Post
                setBody(data)
            }
        }
    }

    suspend fun getCustomCache(namespace: String): String {
        return withContext(Dispatchers.IO) {
            val response = client.request("$ip:$port/cache/$namespace") {
                method = HttpMethod.Get
            }
            response.bodyAsText()
        }
    }

    suspend fun cachePlayer(syncedPlayer: SyncedPlayer) = coroutineScope {
        launch {
            client.request("$ip:$port/cache/player/${syncedPlayer.uuid}") {
                method = HttpMethod.Post
                setBody(Gson().toJson(syncedPlayer))
            }
        }
    }

    suspend fun getPlayerCache(): List<SyncedPlayer> {
        // TODO
        throw NotImplementedError()
    }

    suspend fun getPlayerCache(uuid: UUID): SyncedPlayer? {
        return withContext(Dispatchers.IO) {
            val response = client.request("$ip:$port/player/$uuid") {
                method = HttpMethod.Get
            }
            if (response.status == HttpStatusCode.NotFound) {
                return@withContext null
            }
            val json = response.bodyAsText()
            return@withContext Gson().fromJson(json, SyncedPlayer::class.java)
        }
    }

    suspend fun cachePlayerData(uuid: UUID, data: HashMap<String, String>) = coroutineScope {
        launch {
            client.request("$ip:$port/player/$uuid/data") {
                method = HttpMethod.Post
                setBody(Gson().toJson(data))
            }
        }
    }

    suspend fun getPlayerData(uuid: UUID): HashMap<String, String> {
        return withContext(Dispatchers.IO) {
            val response = client.request("$ip:$port/player/$uuid/data") {
                method = HttpMethod.Get
            }
            val json = response.bodyAsText()
            // TODO
            throw NotImplementedError()
        }
    }

    suspend fun cachePlayerData(uuid: UUID, key: String, value: String) = coroutineScope {
        launch {
            client.request("$ip:$port/player/$uuid/data/$key") {
                method = HttpMethod.Post
                setBody(value)
            }
        }
    }

    suspend fun getPlayerData(uuid: UUID, key: String): String {
        return withContext(Dispatchers.IO) {
            val response = client.request("$ip:$port/player/$uuid/data/$key") {
                method = HttpMethod.Get
            }
            return@withContext response.bodyAsText()
        }
    }

    suspend fun getOrCachePlayer(uuid: UUID, default: SyncedPlayer): SyncedPlayer? {
        val cached = getPlayerCache(uuid)
        if (cached != null) {
            return cached
        }
        cachePlayer(default)
        return null
    }

    suspend fun sendPacket(packet: SyncPacket, target: List<String>, broadcast: Boolean, await: Boolean): String? {
        //outgoingPackets += packet

        val uuid = UUID.randomUUID()
        val data = Gson().toJson(packet)

        val obj = JsonObject()
        obj.addProperty("packetId", uuid.toString())
        obj.addProperty("sentFrom", serverId)
        obj.add("targetServers", JsonParser.parseString(target.toString()).asJsonArray)
        obj.addProperty("data", data)
        obj.addProperty("broadcast", broadcast)
        obj.addProperty("awaitResponse", await)

        val session = socketConnection ?: return null
        session.send(obj.toString())
        println("Packet sent!")

        if (!await || broadcast) {
            return null
        }

        val deferredResponse = CompletableDeferred<String>()
        awaiting[uuid] = deferredResponse to System.currentTimeMillis()

        return deferredResponse.await()
    }

    internal suspend fun start() = coroutineScope {
        launch {
            println("Starting!")
            client.webSocket(HttpMethod.Get, ip, port, "/waves-sync-packets", request = {
                this.parameter("server-id", serverId)
            }) {

                socketConnection = this

                try {
                    for (frame in incoming) {
                        println("Received Message...")
                        frame as? Frame.Text ?: continue
                        val packet = frame.readText()
                        handlePacket(packet)
                    }

                } catch (e: Exception) {
                    println(e.localizedMessage)
                } finally {
                    println("Closing")
                    close()
                }

            }
            client.close()
            println("Server is offline, disconnecting!")
            Bukkit.shutdown()
        }
    }

    private suspend fun handlePacket(packet: String) {
        val json = JsonParser.parseString(packet).asJsonObject
        val id = json.get("packetId").asString
        val response = json.get("awaitResponse").asBoolean
        val data = json.get("data").asJsonObject

        val toRespond = SyncHandler.handlePacket(data)
        if (response) {
            SyncHandler.sendPacket(
                PacketResponse(id, toRespond ?: "null"), json.get("sentFrom").asString
            )
        }
    }

}