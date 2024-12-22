package gg.aquatic.waves.menu.settings

import gg.aquatic.waves.util.requirement.ConfiguredRequirement
import gg.aquatic.waves.inventory.event.AsyncPacketInventoryInteractEvent
import gg.aquatic.waves.menu.AquaticMenu
import gg.aquatic.waves.menu.PrivateAquaticMenu
import gg.aquatic.waves.menu.component.AnimatedButton
import gg.aquatic.waves.util.checkRequirements
import org.bukkit.entity.Player
import java.util.TreeMap

class AnimatedButtonSettings(
    val id: String,
    val frames: TreeMap<Int, IButtonSettings>,
    val viewRequirements: Collection<ConfiguredRequirement<Player>>,
    val click: ClickSettings?,
    val priority: Int,
    val updateEvery: Int,
    val failComponent: IButtonSettings?
) : IButtonSettings {
    override fun create(updater: (String, AquaticMenu) -> String, click: (AsyncPacketInventoryInteractEvent) -> Unit): AnimatedButton {
        return AnimatedButton(
            id,
            TreeMap(frames.mapValues { it.value.create(updater) }),
            priority,
            updateEvery,
            failComponent?.create(updater),
            { menu ->
                if (menu is PrivateAquaticMenu) {
                    viewRequirements.checkRequirements(menu.player)
                } else true
            },
            updater,
            { e ->
                click(e)
                this.click?.handleClick(e) { _, s -> updater(s, e.inventory as AquaticMenu) }
            }
        )
    }
}