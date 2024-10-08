package gg.aquatic.waves.sync

import gg.aquatic.aquaticseries.lib.util.runSync
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import org.bukkit.Bukkit

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
    fun sendPacket(packet: String, target: String) {
        //outgoingPackets += packet

        GlobalScope.launch {
            val session = socketConnection ?: return@launch
            session.send(packet)
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