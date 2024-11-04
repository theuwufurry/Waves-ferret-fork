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
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object ItemHandler : WaveModule {

    val NAMESPACE_KEY by lazy {
        NamespacedKey(AquaticSeriesLib.INSTANCE.plugin, "Custom_Item_Registry")
    }
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
                    it.player, aitem, it, it.action == Action.LEFT_CLICK_AIR || it.action == Action.LEFT_CLICK_BLOCK,
                )
                interaction(aitemEvent)
                aitemEvent.call()
            }
        }
    }

    override fun disable(waves: Waves) {

    }

    fun create(
        item: ItemStack,
        name: String? = null,
        description: MutableList<String>? = null,
        amount: Int = 1,
        modeldata: Int = -1,
        enchantments: MutableMap<String, Int>? = null,
        flags: MutableList<ItemFlag>? = null,
        spawnerEntityType: EntityType? = null
    ): AquaticItem {
        return AquaticItem(
            item,
            name,
            description,
            amount,
            modeldata,
            enchantments,
            flags,
            spawnerEntityType
        )
    }

    interface Factory {

        fun create(id: String): ItemStack?

    }

}