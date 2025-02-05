package gg.aquatic.waves.util.statistic.impl

import com.willfp.eco.core.gui.player
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.event.event
import gg.aquatic.waves.util.statistic.StatisticAddEvent
import gg.aquatic.waves.util.statistic.StatisticType
import org.bukkit.entity.Animals
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.CraftItemEvent

object KillStatistic: StatisticType<Player>() {
    override val arguments: Collection<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("mobs", Boolean, true),
        PrimitiveObjectArgument("animals", Boolean, true),
        PrimitiveObjectArgument("players", Boolean, true)
    )

    private var listener: Listener? = null

    override fun initialize() {
        EntityDamageByEntityEvent.getHandlerList()
        listener = event<EntityDamageByEntityEvent>(ignoredCancelled = true) {
            val player = it.damager as? Player ?: return@event

            for (statisticHandle in handles) {
                val args = statisticHandle.args
                val mobs = args.boolean("mobs") ?: true
                val animals = args.boolean("animals") ?: true
                val players = args.boolean("players") ?: true

                if ((it.entity is Player && !players) ||
                    (it.entity is Animals && !animals) ||
                    (it.entity is Monster && !mobs)) {
                    continue
                }

                val event = StatisticAddEvent(this, 1, player)
                statisticHandle.consumer(event)
            }
        }
    }

    override fun terminate() {
        listener?.let { HandlerList.unregisterAll(it) }
        listener = null
    }
}