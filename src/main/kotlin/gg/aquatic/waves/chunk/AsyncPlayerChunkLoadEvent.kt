package gg.aquatic.waves.chunk

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData
import gg.aquatic.aquaticseries.lib.util.AquaticEvent
import org.bukkit.Chunk
import org.bukkit.entity.Player

class AsyncPlayerChunkLoadEvent(
    val player: Player,
    val chunk: Chunk,
    val wrappedPacket: WrapperPlayServerChunkData
): AquaticEvent(true) {
}