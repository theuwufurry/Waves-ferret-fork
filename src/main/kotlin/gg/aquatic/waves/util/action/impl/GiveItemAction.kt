package gg.aquatic.waves.util.action.impl

import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.util.argument.impl.ItemObjectArgument
import gg.aquatic.waves.util.item.toCustomItem
import org.bukkit.Material
import org.bukkit.entity.Player

class GiveItemAction: AbstractAction<Player>() {
    override fun execute(binder: Player, args: Map<String, Any?>, textUpdater: (Player, String) -> String) {
        val customItem = args["item"] as? AquaticItem ?: return
        customItem.giveItem(binder)
    }

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        ItemObjectArgument("item", Material.STONE.toCustomItem(), true)
    )
}