package gg.aquatic.waves.hologram

import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

class AquaticHologram {

    val lines = ConcurrentHashMap.newKeySet<HologramLine>()
    val viewers = ConcurrentHashMap<Player,MutableSet<SpawnedHologramLine>>()

    fun tick() {
        tickRange()
        viewers.forEach { (_, lines) ->
            // CurrentLineIndex -> Get Hologram line -> Compare Hologram Line with SpawnedHologramLine
            // If it is the same, then skip & add index, otherwise update line, add to the set & move other lines

            // First process all line text update & visibility and then apply changes
            for (line in lines) {

                line.update()
            }
        }
    }

    private fun tickRange() {

    }

}