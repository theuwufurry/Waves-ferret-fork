package gg.aquatic.waves.hologram

import org.bukkit.Location
import org.bukkit.entity.Player

abstract class HologramLine {

    abstract val height: Double
    abstract val filter: (Player) -> Boolean
    abstract val failLine: HologramLine?

    abstract fun spawn(location: Location, player: Player, textUpdater: (Player, String) -> String): SpawnedHologramLine

    internal abstract fun destroy(spawnedHologramLine: SpawnedHologramLine)

    internal abstract fun update(spawnedHologramLine: SpawnedHologramLine)

    internal abstract fun move(spawnedHologramLine: SpawnedHologramLine)

    internal abstract fun createEntity(spawnedHologramLine: SpawnedHologramLine)
}