package gg.aquatic.waves.sync

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import gg.aquatic.aquaticseries.lib.util.runSync
import gg.aquatic.waves.sync.internpacket.NetworkPacket
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
        install (WebSockets) {
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

    val awaiting = HashMap<UUID,CompletableDeferred<String>>()

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

    suspend fun sendPacket(packet: SyncPacket, target: List<String>, broadcast: Boolean, await: Boolean): String? {
        //outgoingPackets += packet

        val networkPacket = NetworkPacket(packet, await)
        val data = Gson().toJson(networkPacket)

        val obj = JsonObject()
        obj.addProperty("packetId", UUID.randomUUID().toString())
        obj.addProperty("sentFrom", serverId)
        obj.add("targetServers", JsonParser.parseString(target.toString()).asJsonArray)
        obj.addProperty("data", data)
        obj.addProperty("broadcast", broadcast)

        val session = socketConnection ?: return null
        session.send(obj.toString())
        println("Packet sent!")

        if (!await || broadcast) {
            return null
        }

        val deferredResponse = CompletableDeferred<String>()
        awaiting[networkPacket.packetId] = deferredResponse

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

                        runSync {
                            handlePacket(packet)
                        }
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

    private fun handlePacket(packet: String) {

    }

}