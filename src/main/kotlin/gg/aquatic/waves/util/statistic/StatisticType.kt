package gg.aquatic.waves.util.statistic

import gg.aquatic.waves.util.argument.AquaticObjectArgument
import org.bukkit.entity.Player

abstract class StatisticType {

    abstract val arguments: Collection<AquaticObjectArgument<*>>

    val handles = mutableListOf<StatisticHandle>()

    abstract fun initialize()
    abstract fun terminate()

    fun registerHandle(handle: StatisticHandle) {
        if (handles.isEmpty()) {
            initialize()
        }
        handles.add(handle)
    }

    fun unregisterHandle(handle: StatisticHandle) {
        handles.remove(handle)
        if (handles.isEmpty()) {
            terminate()
        }
    }
}

class StatisticHandle(
    val statistic: StatisticType,
    val args: Map<String, Any?>,
    val consumer: (StatisticAddEvent) -> Unit
) {

    fun unregister() {
        statistic.unregisterHandle(this)
    }

    fun register() {
        statistic.registerHandle(this)
    }

}

class StatisticAddEvent(val statistic: StatisticType, val increasedAmount: Number, val player: Player)