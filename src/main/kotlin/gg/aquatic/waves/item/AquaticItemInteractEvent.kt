package gg.aquatic.waves.item

import gg.aquatic.aquaticseries.lib.util.AquaticEvent
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event

class AquaticItemInteractEvent(
    val player: Player,
    val item: AquaticItem,
    val itemStack: org.bukkit.inventory.ItemStack,
    var originalEvent: Event,
    val isLeftClick: Boolean
): AquaticEvent() {

    var isCancelled: Boolean
        get() {
            return (originalEvent as? Cancellable)?.isCancelled ?: false
        }
        set(value) {
            (originalEvent as? Cancellable)?.isCancelled = value
        }

}