package gg.aquatic.waves.hologram

import org.bukkit.Location
import org.bukkit.entity.Player

class SpawnedHologramLine(
    val player: Player,
    val line: HologramLine,
    val entityId: Int,
    location: Location,
    val textUpdater: (Player, String) -> String,
) {
    var currentLocation: Location = location
        private set

    fun update() {
        line.update(this)
    }

    fun move(location: Location) {
        currentLocation = location
        line.move(this)
    }

    fun destroy() {
        line.destroy(this)
    }

}