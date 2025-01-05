package gg.aquatic.waves.hologram

import org.bukkit.configuration.ConfigurationSection

interface LineFactory {

    fun load(section: ConfigurationSection): LineSettings?

}