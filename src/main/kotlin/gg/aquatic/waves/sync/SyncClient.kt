package gg.aquatic.waves.sync

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import gg.aquatic.waves.sync.packet.PacketResponse
import gg.aquatic.wavessync.api.packet.SyncPacket
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
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

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

    val awaiting = ConcurrentHashMap<UUID, Pair<CompletableDeferred<String>,Long>>()

    private var socketConnection: DefaultWebSocketSession? = null

    fun cacheCustom(data: String, namespace: String) {
        runBlocking {
            client.request("$ip:$port/cache/$namespace") {
                method = HttpMethod.Post
                setBody(data)
            }
        }
    }

    fun getPlayerServer(uuid: UUID): String? {
        return runBlocking {
            val response = client.request("$ip:$port/player/$uuid") {
                method = HttpMethod.Get
            }
            val str = response.bodyAsText()
            if (str == "null") {
                null
            } else{
                str
            }
        }
    }
    fun setPlayerServer(uuid: UUID, serverId: String?) {
        runBlocking {
            client.request("$ip:$port/player/$uuid") {
                setBody(serverId ?: "null")
                method = HttpMethod.Post
            }
        }
    }

    fun getCustomCache(namespace: String): String {
        return runBlocking {
            val response = client.request("$ip:$port/cache/$namespace") {
                method = HttpMethod.Get
            }
            response.bodyAsText()
        }
    }

    fun cachePlayer(syncedPlayer: SyncedPlayer) {
        runBlocking {
            client.request("$ip:$port/cache/player/${syncedPlayer.uuid}") {
                method = HttpMethod.Post
                setBody(Gson().toJson(syncedPlayer))
            }
        }
    }

    fun getPlayerCache(): List<SyncedPlayer> {
        // TODO
        throw NotImplementedError()
    }

    fun getPlayerCache(uuid: UUID): SyncedPlayer? {
        return runBlocking {
            val response = client.request("$ip:$port/player/$uuid") {
                method = HttpMethod.Get
            }
            if (response.status == HttpStatusCode.NotFound) {
                return@runBlocking null
            }
            val json = response.bodyAsText()
            Gson().fromJson(json, SyncedPlayer::class.java)
        }
    }

    fun cachePlayerData(uuid: UUID, data: HashMap<String, String>) {
        runBlocking {
            client.request("$ip:$port/player/$uuid/data") {
                method = HttpMethod.Post
                setBody(Gson().toJson(data))
            }
        }
    }

    fun getPlayerData(uuid: UUID): HashMap<String, String>  {
        return runBlocking {
            val response = client.request("$ip:$port/player/$uuid/data") {
                method = HttpMethod.Get
            }
            val json = response.bodyAsText()
            Gson().fromJson(json, HashMap::class.java) as HashMap<String, String>
        }
    }

    fun cachePlayerData(uuid: UUID, key: String, value: String) {
        runBlocking {
            client.request("$ip:$port/player/$uuid/data/$key") {
                method = HttpMethod.Post
                setBody(value)
            }
        }
    }

    fun getPlayerData(uuid: UUID, key: String): String {
        return runBlocking {
            val response = client.request("$ip:$port/player/$uuid/data/$key") {
                method = HttpMethod.Get
            }
            response.bodyAsText()
        }
    }

    fun getOrCachePlayer(uuid: UUID, default: SyncedPlayer): SyncedPlayer? {
        val cached = getPlayerCache(uuid)
        if (cached != null) {
            return cached
        }
        cachePlayer(default)
        return null
    }

    fun sendPacket(packet: SyncPacket, target: List<String>, broadcast: Boolean, await: Boolean): String? {
        //outgoingPackets += packet
        return runBlocking {
            val uuid = UUID.randomUUID()
            val data = Gson().toJson(packet)

            val obj = JsonObject()
            obj.addProperty("packetId", uuid.toString())
            obj.addProperty("sentFrom", serverId)
            obj.addProperty("targetServers", Gson().toJson(target))
            obj.addProperty("data", data)
            obj.addProperty("broadcast", broadcast)
            obj.addProperty("awaitResponse", await)

            val session = socketConnection ?: return@runBlocking null
            session.send(obj.toString())
            println("Packet sent!")

            if (!await || broadcast) {
                return@runBlocking null
            }

            val deferredResponse = CompletableDeferred<String>()
            awaiting[uuid] = deferredResponse to System.currentTimeMillis()

            return@runBlocking deferredResponse.await()
        }
    }

    internal suspend fun start() {
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

    private fun handlePacket(packet: String) {
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