package gg.aquatic.waves.interactable

import org.bukkit.Location
import org.bukkit.entity.Player

abstract class Interactable {

    abstract val location: Location
    abstract val viewers: MutableSet<Player>

    abstract fun addViewer(player: Player)
    abstract fun removeViewer(player: Player)

}