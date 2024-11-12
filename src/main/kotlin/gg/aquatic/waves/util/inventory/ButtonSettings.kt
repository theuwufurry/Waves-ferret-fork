package gg.aquatic.waves.util.inventory

import gg.aquatic.aquaticseries.lib.betterinventory2.component.InventoryComponent
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Function

abstract class ButtonSettings(
    var id: String,
    var priority: Int,
    var viewConditions: HashMap<Function<Player, Boolean>, ButtonSettings?>,
    var failItem: ButtonSettings?,
    var onClick: ClickSettings,
    var updateEvery: Int,
) {

    abstract fun create(textUpdater: BiFunction<Player, String, String>, callback: Consumer<InventoryClickEvent>): InventoryComponent
}