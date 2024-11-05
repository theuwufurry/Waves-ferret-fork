package gg.aquatic.waves.util

import org.bukkit.configuration.ConfigurationSection

fun ConfigurationSection.keysForEach(path: String, boolean: Boolean, function: (String) -> Unit) {
    val section = getConfigurationSection(path) ?: return
    section.keysForEach(boolean, function)
}

fun ConfigurationSection.keysForEach(boolean: Boolean, function: (String) -> Unit) {
    getKeys(boolean).forEach(function)
}