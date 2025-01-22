package gg.aquatic.waves.util.statistic

import gg.aquatic.waves.util.argument.AquaticObjectArgument

abstract class StatisticType<T> {

    abstract val arguments: Collection<AquaticObjectArgument<*>>

    val handles = mutableListOf<StatisticHandle<T>>()

    abstract fun initialize()
    abstract fun terminate()

    fun registerHandle(handle: StatisticHandle<T>) {
        if (handles.isEmpty()) {
            initialize()
        }
        handles.add(handle)
    }

    fun unregisterHandle(handle: StatisticHandle<T>) {
        handles.remove(handle)
        if (handles.isEmpty()) {
            terminate()
        }
    }
}

class StatisticHandle<T>(
    val statistic: StatisticType<T>,
    val args: Map<String, Any?>,
    val consumer: (StatisticAddEvent<T>) -> Unit
) {

    fun unregister() {
        statistic.unregisterHandle(this)
    }

    fun register() {
        statistic.registerHandle(this)
    }

}

class StatisticAddEvent<T>(val statistic: StatisticType<T>, val increasedAmount: Number, val binder: T)