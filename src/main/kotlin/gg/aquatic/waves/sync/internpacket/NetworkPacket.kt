package gg.aquatic.waves.sync.internpacket

import gg.aquatic.waves.sync.packet.SyncPacket
import java.util.*

class NetworkPacket(
    val packet: SyncPacket,
    val awaitResponse: Boolean
) {
    val packetId: UUID = UUID.randomUUID()
}