package gg.aquatic.waves.item

import gg.aquatic.aquaticseries.lib.AquaticSeriesLib
import gg.aquatic.aquaticseries.lib.util.call
import gg.aquatic.aquaticseries.lib.util.event
import gg.aquatic.aquaticseries.lib.util.runSync
import gg.aquatic.waves.Waves
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.registry.isAquaticItem
import gg.aquatic.waves.registry.registryId
import gg.aquatic.waves.util.item.AquaticItemInteractEvent
import org.bukkit.NamespacedKey
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

object ItemHandler : WaveModule {

    val NAMESPACE_KEY = NamespacedKey(AquaticSeriesLib.INSTANCE.plugin, "Custom_Item_Registry")
    val listenInteractions = mutableMapOf<String, (AquaticItemInteractEvent) -> Unit>()
    override val type: WaveModules = WaveModules.ITEMS

    override suspend fun initialize(waves: Waves) {
        runSync {
            event<PlayerInteractEvent> {
                if (it.hand == EquipmentSlot.OFF_HAND) return@event
                if (listenInteractions.isEmpty()) return@event
                val item = it.item ?: return@event
                val aitem = item.isAquaticItem() ?: return@event
                val registry = aitem.registryId() ?: return@event

                val interaction = listenInteractions[registry] ?: return@event

                val aitemEvent = AquaticItemInteractEvent(
                    it.player, aitem, it
                )
                interaction(aitemEvent)
                aitemEvent.call()
            }
        }
    }

    override fun disable(waves: Waves) {

    }

}