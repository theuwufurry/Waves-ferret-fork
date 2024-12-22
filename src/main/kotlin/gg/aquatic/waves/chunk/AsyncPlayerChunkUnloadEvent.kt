package gg.aquatic.waves.chunk

import gg.aquatic.waves.util.event.AquaticEvent
import org.bukkit.Chunk
import org.bukkit.entity.Player

class AsyncPlayerChunkUnloadEvent(
    val player: Player,
    val chunk: Chunk
): AquaticEvent(true) {
}