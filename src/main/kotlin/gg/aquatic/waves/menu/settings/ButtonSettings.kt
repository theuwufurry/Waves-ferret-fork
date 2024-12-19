package gg.aquatic.waves.menu.settings

import gg.aquatic.aquaticseries.lib.requirement.ConfiguredRequirement
import gg.aquatic.aquaticseries.lib.util.checkRequirements
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.menu.AquaticMenu
import gg.aquatic.waves.menu.PrivateAquaticMenu
import gg.aquatic.waves.menu.component.Button
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

    override fun create(updater: (String, AquaticMenu) -> String): Button {
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
                click?.handleClick(e) { _, s -> updater(s, e.inventory as AquaticMenu) }
            }
        )
    }

}