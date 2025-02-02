package gg.aquatic.waves.menu.settings

import gg.aquatic.waves.util.requirement.ConfiguredRequirement
import gg.aquatic.waves.inventory.event.AsyncPacketInventoryInteractEvent
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.menu.AquaticMenu
import gg.aquatic.waves.menu.PrivateAquaticMenu
import gg.aquatic.waves.menu.component.Button
import gg.aquatic.waves.util.collection.checkRequirements
import org.bukkit.entity.Player

class ButtonSettings(
    val id: String,
    val item: AquaticItem?,
    val slots: Collection<Int>,
    val viewRequirements: Collection<ConfiguredRequirement<Player>>,
    val click: ClickSettings?,
    val priority: Int,
    val updateEvery: Int,
    val failComponent: IButtonSettings?
) : IButtonSettings {

    override fun create(updater: (String, AquaticMenu) -> String, click: (AsyncPacketInventoryInteractEvent) -> Unit): Button {
        return Button(
            id,
            item?.getItem(),
            slots,
            priority,
            updateEvery,
            failComponent?.create(updater),
            { menu: AquaticMenu ->
                if (menu is PrivateAquaticMenu) {
                    viewRequirements.checkRequirements(menu.player)
                } else
                    true
            },
            updater,
            { e ->
                click(e)
                this.click?.handleClick(e) { _, s -> updater(s, e.inventory as AquaticMenu) }
            }
        )
    }

}