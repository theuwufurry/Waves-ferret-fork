package gg.aquatic.waves.interactable.type

import gg.aquatic.waves.fake.block.FakeBlock
import gg.aquatic.waves.interactable.Interactable
import gg.aquatic.waves.interactable.InteractableInteractEvent
import gg.aquatic.waves.util.audience.AquaticAudience
import org.bukkit.Location
import org.bukkit.entity.Player

class BlockInteractable(
    val block: FakeBlock, override val onInteract: (InteractableInteractEvent) -> Unit
) : Interactable() {

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

    init {
        block.onInteract = { e ->
            this.onInteract(
                InteractableInteractEvent(
                    this,
                    e.player,
                    e.isLeftClick
                )
            )
        }
    }

    override fun addViewer(player: Player) {
        block.addViewer(player)
    }

    override fun removeViewer(player: Player) {
        block.removeViewer(player)
    }

    override fun destroy() {
        this.block.destroy()
    }

    override fun updateViewers() {
        block.tickRange(true)
    }

}