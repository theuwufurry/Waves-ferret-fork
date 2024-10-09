package gg.aquatic.waves.sync

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import gg.aquatic.aquaticseries.lib.util.runSync
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
import java.util.concurrent.CompletableFuture

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

    private var socketConnection: DefaultWebSocketSession? = null

    @OptIn(DelicateCoroutinesApi::class)
    fun cacheCustom(data: String, namespace: String): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        GlobalScope.launch {
            client.request("$ip:$port/cache/$namespace") {
                method = HttpMethod.Post
                setBody(data)
            }
            future.complete(null)
        }
        return future
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getCustomCache(namespace: String): CompletableFuture<String?> {
        val future = CompletableFuture<String?>()
        GlobalScope.launch {
            val response = client.request("$ip:$port/cache/$namespace") {
                method = HttpMethod.Get
            }
            future.complete(response.bodyAsText())
        }
        return future
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun sendPacket(packet: String, target: List<String>, broadcast: Boolean) {
        //outgoingPackets += packet

        val obj = JsonObject()
        obj.addProperty("packetId", UUID.randomUUID().toString())
        obj.addProperty("sentFrom", serverId)
        obj.add("targetServers", JsonParser.parseString(target.toString()).asJsonArray)
        obj.addProperty("data", packet)
        obj.addProperty("broadcast", broadcast)

        GlobalScope.launch {
            val session = socketConnection ?: return@launch
            session.send(obj.toString())
            println("Packet sent!")
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    internal fun start() {

        GlobalScope.launch {
            withContext(Dispatchers.IO) {

                println("Starting!")
                runBlocking {
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
                }
                client.close()
                Bukkit.shutdown()
            }
        }
    }

    private fun handlePacket(packet: String) {

    }

}