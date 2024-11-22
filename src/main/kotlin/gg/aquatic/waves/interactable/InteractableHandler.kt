package gg.aquatic.waves.interactable

import gg.aquatic.aquaticseries.lib.util.event
import gg.aquatic.waves.Waves
import gg.aquatic.waves.interactable.type.MEGInteractable
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent

object InteractableHandler: WaveModule {

    /*
    val blockInteractables = mutableListOf<BlockInteractable>()
    val entityInteractables = mutableListOf<EntityInteractable>()
     */
    val megInteractables = mutableListOf<MEGInteractable>()
    override val type: WaveModules = WaveModules.INTERACTABLES

    override fun initialize(waves: Waves) {
        if (Bukkit.getPluginManager().getPlugin("ModelEngine") != null) {
            MEGInteractableHandler()
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