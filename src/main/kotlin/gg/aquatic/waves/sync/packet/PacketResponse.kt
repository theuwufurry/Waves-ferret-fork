package gg.aquatic.waves.sync.packet

import com.google.gson.Gson
import gg.aquatic.waves.sync.SyncPacketHandler

class PacketResponse(
    val response: String
): SyncPacket() {
    override val packetType: String = "waves_packet_response"

    object Handler: SyncPacketHandler<PacketResponse> {
        override fun handle(packet: PacketResponse) {
            println("Received packet response")
        }

        override fun serialize(json: String): PacketResponse {
            return Gson().fromJson(json, PacketResponse::class.java)
        }

        override fun deserialize(packet: PacketResponse): String {
            return Gson().toJson(packet)
        }
    }
}