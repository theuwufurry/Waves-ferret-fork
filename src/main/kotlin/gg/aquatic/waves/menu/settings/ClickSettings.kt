package gg.aquatic.waves.menu.settings

import gg.aquatic.aquaticseries.lib.betterinventory2.action.ConfiguredActionsWithConditions
import gg.aquatic.waves.inventory.ButtonType
import gg.aquatic.waves.inventory.event.AsyncPacketInventoryInteractEvent
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import java.util.function.BiFunction

class ClickSettings(
    val clicks: HashMap<ButtonType,MutableList<ConfiguredActionsWithConditions>>,
) {

    fun handleClick(event: AsyncPacketInventoryInteractEvent, updater: BiFunction<Player, String, String>) {
        val type = event.buttonType
        val actions = clicks[type] ?: return
        for (action in actions) {
            action.tryRun(event.viewer.player, updater)
        }
    }
}