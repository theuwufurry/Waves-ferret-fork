package gg.aquatic.waves.interactable.type

import gg.aquatic.waves.fake.npc.FakePlayer
import gg.aquatic.waves.interactable.Interactable
import gg.aquatic.waves.interactable.InteractableInteractEvent
import gg.aquatic.waves.util.audience.AquaticAudience
import org.bukkit.Location
import org.bukkit.entity.Player

class NPCInteractable(val npc: FakePlayer, override val onInteract: (InteractableInteractEvent) -> Unit) : Interactable() {

    override var audience: AquaticAudience
        get() {
            return npc.audience
        }
        set(value) {
            npc.audience = value
        }

    override val location: Location
        get() {
            return npc.location
        }
    override val viewers: MutableSet<Player>
        get() {
            return npc.viewers
        }

    init {
        npc.onInteract = { e ->
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
        npc.addViewer(player)
    }

    override fun removeViewer(player: Player) {
        npc.removeViewer(player)
    }

    override fun destroy() {
        this.npc.destroy()
    }

    override fun updateViewers() {
        npc.tickRange(true)
    }
}