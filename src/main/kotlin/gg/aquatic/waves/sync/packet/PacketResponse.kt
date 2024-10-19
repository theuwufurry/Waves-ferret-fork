package gg.aquatic.waves.sync.packet

import com.google.gson.Gson
import gg.aquatic.waves.sync.SyncHandler
import gg.aquatic.waves.sync.SyncPacketHandler
import gg.aquatic.wavessync.api.packet.SyncPacket
import java.util.*

class PacketResponse(
    val originalId: String,
    val response: String
): SyncPacket() {
    override val packetType: String = "waves_packet_response"

    object Handler: SyncPacketHandler<PacketResponse> {
        override suspend fun handle(packet: PacketResponse): String? {
            println("Received packet response")

            val awaiting = SyncHandler.client.awaiting
            val uuid = UUID.fromString(packet.originalId)
            val awaitingPacket = awaiting[uuid] ?: return null
            awaitingPacket.first.complete(packet.response)
            awaiting.remove(uuid)
            return null
        }

        override fun serialize(json: String): PacketResponse {
            return Gson().fromJson(json, PacketResponse::class.java)
        }

        override fun deserialize(packet: PacketResponse): String {
            return Gson().toJson(packet)
        }
    }
}