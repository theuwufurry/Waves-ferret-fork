package gg.aquatic.waves.interactable.type

import gg.aquatic.aquaticseries.lib.audience.AquaticAudience
import gg.aquatic.waves.fake.block.FakeBlock
import gg.aquatic.waves.interactable.Interactable
import org.bukkit.Location
import org.bukkit.entity.Player

class BlockInteractable(
    val block: FakeBlock
): Interactable() {

    override var audience: AquaticAudience
        get() {
            return block.audience
        }
        set(value) {
            block.audience = value
        }

    override val location: Location
        get() {
            return block.location
        }
    override val viewers: MutableSet<Player>
        get() {
            return block.viewers
        }

    override fun addViewer(player: Player) {
        block.addViewer(player)
    }

    override fun removeViewer(player: Player) {
        block.removeViewer(player)
    }

}