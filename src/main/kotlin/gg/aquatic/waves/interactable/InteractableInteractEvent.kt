package gg.aquatic.waves.interactable

import gg.aquatic.waves.util.event.AquaticEvent
import org.bukkit.entity.Player

class InteractableInteractEvent(
    val interactable: Interactable,
    val player: Player,
    val isLeft: Boolean
): AquaticEvent() {
}