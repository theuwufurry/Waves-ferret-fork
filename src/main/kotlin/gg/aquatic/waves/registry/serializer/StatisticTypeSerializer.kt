package gg.aquatic.waves.registry.serializer

import gg.aquatic.waves.registry.WavesRegistry
import gg.aquatic.waves.util.argument.ArgumentSerializer
import gg.aquatic.waves.util.statistic.StatisticAddEvent
import gg.aquatic.waves.util.statistic.StatisticHandle
import org.bukkit.configuration.ConfigurationSection

object StatisticTypeSerializer {

    fun fromSection(configurationSection: ConfigurationSection, consumer: (StatisticAddEvent) -> Unit): StatisticHandle? {
        val typeId = configurationSection.getString("type") ?: return null
        val type = WavesRegistry.STATISTIC_TYPES[typeId] ?: return null
        val args = ArgumentSerializer.load(configurationSection, type.arguments)
        return StatisticHandle(type, args, consumer)
    }

    fun fromSections(configurationSections: List<ConfigurationSection>, consumer: (StatisticAddEvent) -> Unit): List<StatisticHandle> {
        return configurationSections.mapNotNull { fromSection(it, consumer) }
    }

}