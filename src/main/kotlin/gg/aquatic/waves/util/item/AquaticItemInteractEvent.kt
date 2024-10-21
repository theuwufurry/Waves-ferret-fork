package gg.aquatic.waves.util.item

import gg.aquatic.aquaticseries.lib.item2.AquaticItem
import gg.aquatic.aquaticseries.lib.util.AquaticEvent
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event

class AquaticItemInteractEvent(
    val player: Player,
    val item: AquaticItem,
    var originalEvent: Event
): AquaticEvent() {

    var isCancelled: Boolean
        get() {
            return (originalEvent as? Cancellable)?.isCancelled ?: false
        }
        set(value) {
            (originalEvent as? Cancellable)?.isCancelled = value
        }

}