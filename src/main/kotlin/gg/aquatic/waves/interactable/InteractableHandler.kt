package gg.aquatic.waves.interactable

import com.ticxo.modelengine.api.events.BaseEntityInteractEvent
import com.ticxo.modelengine.api.events.BaseEntityInteractEvent.Action
import gg.aquatic.aquaticseries.lib.util.event
import gg.aquatic.waves.Waves
import gg.aquatic.waves.interactable.type.MEGInteractable
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.EquipmentSlot

object InteractableHandler: WaveModule {

    /*
    val blockInteractables = mutableListOf<BlockInteractable>()
    val entityInteractables = mutableListOf<EntityInteractable>()
     */
    val megInteractables = mutableListOf<MEGInteractable>()
    override val type: WaveModules = WaveModules.INTERACTABLES

    override fun initialize(waves: Waves) {
        event<BaseEntityInteractEvent> {
            val base = it.baseEntity
            if (base !is MEGInteractableDummy) return@event
            val interactable = base.interactable
            if (it.slot == EquipmentSlot.OFF_HAND) return@event
            val event = InteractableInteractEvent(
                interactable,
                it.player,
                it.action == Action.ATTACK
            )
            interactable.onInteract(event)
        }
        event<PlayerJoinEvent> {
            for (tickableObject in megInteractables) {
                if (tickableObject.audience.canBeApplied(it.player)) {
                    tickableObject.addViewer(it.player)
                }
            }
        }
    }

    override fun disable(waves: Waves) {

    }

}