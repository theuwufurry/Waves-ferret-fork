package gg.aquatic.waves.sync

import gg.aquatic.waves.sync.packet.SyncPacket

interface SyncPacketHandler<T: SyncPacket> {

    fun handle(packet: T)
    fun serialize(json: String): T
    fun deserialize(packet: T): String

    fun serializeAndHandle(json: String) {
        val packet = serialize(json)
        handle(packet)
    }

}