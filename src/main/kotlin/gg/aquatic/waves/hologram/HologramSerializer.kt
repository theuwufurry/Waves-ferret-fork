package gg.aquatic.waves.hologram

import gg.aquatic.waves.registry.WavesRegistry
import gg.aquatic.waves.registry.serializer.RequirementSerializer
import gg.aquatic.waves.util.getSectionList
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

object HologramSerializer {

    fun loadLine(section: ConfigurationSection): LineSettings? {
        val typeId = section.getString("type", "text")?.lowercase() ?: return null
        val type = WavesRegistry.HOLOGRAM_LINE_FACTORIES[typeId] ?: return null

        return type.load(section)
    }

    fun loadLines(sections: List<ConfigurationSection>): Set<LineSettings> {
        return sections.mapNotNull { loadLine(it) }.toSet()
    }

    fun loadHologram(section: ConfigurationSection): AquaticHologram.Settings {
        val lines = loadLines(section.getSectionList("lines"))
        val conditions = RequirementSerializer.fromSections<Player>(section.getSectionList("view-requirements"))
        val viewDistance = section.getInt("view-distance", 100)
        return AquaticHologram.Settings(lines, conditions, viewDistance)
    }

}