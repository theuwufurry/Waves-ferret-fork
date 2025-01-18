package gg.aquatic.waves.util.block.factory

import gg.aquatic.waves.util.block.AquaticBlock
import gg.aquatic.waves.util.block.impl.OraxenBlock
import io.th0rgal.oraxen.api.OraxenBlocks
import org.bukkit.configuration.ConfigurationSection

object OraxenFactory : BlockFactory {
    override fun load(section: ConfigurationSection, material: String): AquaticBlock {
        return OraxenBlock(material)
    }
}