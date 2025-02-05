package gg.aquatic.waves.util.statistic.impl

import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.event.event
import gg.aquatic.waves.util.statistic.StatisticAddEvent
import gg.aquatic.waves.util.statistic.StatisticType
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent

object DamageDealtStatistic: StatisticType<Player>() {
    override val arguments: Collection<AquaticObjectArgument<*>> = listOf()

    private var listener: Listener? = null

    override fun initialize() {
        EntityDamageByEntityEvent.getHandlerList()
        listener = event<EntityDamageByEntityEvent>(ignoredCancelled = true) {
            val player = it.damager as? Player ?: return@event

            for (statisticHandle in handles) {
                val event = StatisticAddEvent(this, it.damage, player)
                statisticHandle.consumer(event)
            }
        }
    }

    override fun terminate() {
        listener?.let { HandlerList.unregisterAll(it) }
        listener = null
    }
}