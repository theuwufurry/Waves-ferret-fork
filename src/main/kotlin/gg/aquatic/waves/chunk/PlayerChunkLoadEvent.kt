package gg.aquatic.waves.chunk

import gg.aquatic.aquaticseries.lib.util.AquaticEvent
import org.bukkit.Chunk
import org.bukkit.entity.Player

class PlayerChunkLoadEvent(
    val player: Player,
    val chunk: Chunk
): AquaticEvent() {
}