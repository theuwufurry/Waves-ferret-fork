package gg.aquatic.waves.util

import gg.aquatic.aquaticseries.lib.item2.AquaticItem
import gg.aquatic.waves.registry.serializer.ItemSerializer
import kotlinx.coroutines.runBlocking
import org.bukkit.configuration.ConfigurationSection

suspend fun AquaticItem.Companion.loadFromYml(section: ConfigurationSection?): AquaticItem? {
    return ItemSerializer.fromSection(section)
}