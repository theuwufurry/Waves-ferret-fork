package gg.aquatic.waves.sync

import gg.aquatic.wavessync.api.packet.SyncPacket

interface SyncPacketHandler<T: SyncPacket> {

    fun handle(packet: T): String?
    fun serialize(json: String): T
    fun deserialize(packet: T): String

    fun serializeAndHandle(json: String): String? {
        val packet = serialize(json)
        return handle(packet)
    }

}