package gg.aquatic.waves.interactable

import gg.aquatic.aquaticseries.lib.audience.AquaticAudience
import org.bukkit.Location
import org.bukkit.entity.Player

abstract class Interactable {

    abstract val onInteract: (InteractableInteractEvent) -> Unit

    abstract var audience: AquaticAudience
    abstract val location: Location
    abstract val viewers: MutableSet<Player>

    abstract fun addViewer(player: Player)
    abstract fun removeViewer(player: Player)

    abstract fun destroy()

}