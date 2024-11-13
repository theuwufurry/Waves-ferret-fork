package gg.aquatic.waves.interactable

import com.ticxo.modelengine.api.events.BaseEntityInteractEvent
import gg.aquatic.aquaticseries.lib.util.event
import gg.aquatic.waves.Waves
import gg.aquatic.waves.interactable.type.BlockInteractable
import gg.aquatic.waves.interactable.type.EntityInteractable
import gg.aquatic.waves.interactable.type.MEGInteractable
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules

object InteractableHandler: WaveModule {

    val blockInteractables = mutableListOf<BlockInteractable>()
    val entityInteractables = mutableListOf<EntityInteractable>()
    val megInteractables = mutableListOf<MEGInteractable>()
    override val type: WaveModules = WaveModules.INTERACTABLES

    override fun initialize(waves: Waves) {
        event<BaseEntityInteractEvent> {

        }
    }

    override fun disable(waves: Waves) {

    }

}