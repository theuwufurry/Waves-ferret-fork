package gg.aquatic.waves.sync

import gg.aquatic.waves.sync.packet.SyncPacket

interface SyncPacketHandler<T: SyncPacket> {

    suspend fun handle(packet: T): String?
    fun serialize(json: String): T
    fun deserialize(packet: T): String

    suspend fun serializeAndHandle(json: String): String? {
        val packet = serialize(json)
        return handle(packet)
    }

}