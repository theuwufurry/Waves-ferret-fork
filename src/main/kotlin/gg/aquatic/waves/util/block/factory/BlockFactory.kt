package gg.aquatic.waves.util.block.factory

import gg.aquatic.waves.util.block.AquaticBlock
import org.bukkit.configuration.ConfigurationSection

interface BlockFactory {

    fun load(section: ConfigurationSection, material: String): AquaticBlock?

}