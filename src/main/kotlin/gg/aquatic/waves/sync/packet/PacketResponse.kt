package gg.aquatic.waves.sync.packet

import com.google.gson.Gson
import gg.aquatic.waves.sync.SyncPacketHandler

class PacketResponse(
    val originalId: String,
    val response: String
): SyncPacket() {
    override val packetType: String = "waves_packet_response"

    object Handler: SyncPacketHandler<PacketResponse> {
        override suspend fun handle(packet: PacketResponse): String? {
            println("Received packet response")
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