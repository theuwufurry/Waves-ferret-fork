package gg.aquatic.waves.registry.serializer

import gg.aquatic.aquaticseries.lib.betterhologram.AquaticHologram
import gg.aquatic.aquaticseries.lib.betterhologram.impl.ArmorstandLine
import gg.aquatic.aquaticseries.lib.betterhologram.impl.EmptyLine
import gg.aquatic.aquaticseries.lib.betterhologram.impl.ItemDisplayLine
import gg.aquatic.aquaticseries.lib.betterhologram.impl.TextDisplayLine
import gg.aquatic.aquaticseries.lib.item2.AquaticItem
import gg.aquatic.aquaticseries.lib.requirement.ConfiguredRequirement
import gg.aquatic.aquaticseries.lib.util.getSectionList
import gg.aquatic.aquaticseries.lib.util.toAquatic
import gg.aquatic.waves.util.loadFromYml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform
import org.bukkit.entity.Player
import java.util.*
import java.util.function.Function

object HologramSerializer {

    suspend fun load(sections: Collection<ConfigurationSection>): MutableList<AquaticHologram.Line> = withContext(Dispatchers.IO) {
        val list = mutableListOf<AquaticHologram.Line>()
        for (section in sections) {
            val line = loadLine(section) ?: continue
            list += line
        }
        return@withContext list
    }

    suspend fun loadLine(section: ConfigurationSection): AquaticHologram.Line? = withContext(Dispatchers.IO) {
        val type = section.getString("type") ?: return@withContext null
        when (type.lowercase()) {
            "text_display" -> return@withContext loadTextLine(section)
            "item_display" -> return@withContext loadItemLine(section)
            "armorstand" -> return@withContext loadArmorstandLine(section)
            "empty" -> return@withContext loadEmptyLine(section)
        }
        return@withContext null
    }

    suspend fun loadTextLine(section: ConfigurationSection): TextDisplayLine = withContext(Dispatchers.IO) {
        val failLine = loadFailLine(section)
        val requirements = loadRequirements(section)

        val keyframes = TreeMap<Int, TextDisplayLine.TextDisplayKeyframe>()
        if (!section.contains("frames")) {
            keyframes += 0 to loadTextKeyframe(section)
        } else {
            val frames = section.getConfigurationSection("frames")!!
            frames.getKeys(false).forEach {
                keyframes[it.toInt()] = loadTextKeyframe(frames.getConfigurationSection(it)!!)
            }
        }

        return@withContext TextDisplayLine(
            Function { p ->
                for (requirement in requirements) {
                    if (!requirement.check(p)) return@Function false
                }
                return@Function true
            },
            failLine,
            keyframes
        ) { p, line -> line }
    }

    private suspend fun loadTextKeyframe(section: ConfigurationSection): TextDisplayLine.TextDisplayKeyframe = withContext(Dispatchers.IO) {
        val text = section.getString("text")!!
        val height = section.getDouble("height", 0.3)
        val scale = section.getDouble("scale", 1.0).toFloat()
        val billboard = Billboard.valueOf(section.getString("billboard", "CENTER")!!)
        return@withContext TextDisplayLine.TextDisplayKeyframe(
            text.toAquatic(),
            height,
            scale,
            billboard
        )
    }

    suspend fun loadItemLine(section: ConfigurationSection): ItemDisplayLine? = withContext(Dispatchers.IO) {
        val failLine = loadFailLine(section)
        val requirements = loadRequirements(section)

        val keyframes = TreeMap<Int, ItemDisplayLine.ItemDisplayKeyframe>()
        if (!section.contains("frames")) {
            keyframes += 0 to (loadItemKeyframe(section) ?: return@withContext null)
        } else {
            val frames = section.getConfigurationSection("frames")!!
            val keys = frames.getKeys(false)
            Bukkit.getConsoleSender().sendMessage("Keys size: ${keys.size}")
            frames.getKeys(false).forEach {
                Bukkit.getConsoleSender().sendMessage("Loading frame $it")
                val loaded = loadItemKeyframe(frames.getConfigurationSection(it)!!)
                if (loaded != null) {
                    Bukkit.getConsoleSender().sendMessage("Loaded item keyframe at $it")
                    keyframes[it.toInt()] = loaded
                } else {
                    Bukkit.getConsoleSender().sendMessage("Keyframe at $it is null")
                }
            }
        }

        return@withContext ItemDisplayLine(
            Function { p ->
                for (requirement in requirements) {
                    if (!requirement.check(p)) return@Function false
                }
                return@Function true
            },
            failLine,
            keyframes
        )
    }

    private suspend fun loadItemKeyframe(section: ConfigurationSection): ItemDisplayLine.ItemDisplayKeyframe? = withContext(Dispatchers.IO) {
        val item = AquaticItem.loadFromYml(section) ?: return@withContext null
        val height = section.getDouble("height", 0.3)
        val scale = section.getDouble("scale", 1.0).toFloat()
        val billboard = Billboard.valueOf(section.getString("billboard", "CENTER")!!)
        val itemDisplayTransform: ItemDisplayTransform = ItemDisplayTransform.valueOf(section.getString("item-display-transform", "GROUND")!!)
        return@withContext ItemDisplayLine.ItemDisplayKeyframe(
            item.getItem(),
            height,
            scale,
            billboard,
            itemDisplayTransform
        )
    }

    suspend fun loadArmorstandLine(section: ConfigurationSection): ArmorstandLine = withContext(Dispatchers.IO) {
        val failLine = loadFailLine(section)
        val requirements = loadRequirements(section)

        val keyframes = TreeMap<Int, ArmorstandLine.ArmorstandKeyframe>()
        if (!section.contains("frames")) {
            keyframes += 0 to loadArmorstandKeyframe(section)
        } else {
            val frames = section.getConfigurationSection("frames")!!
            frames.getKeys(false).forEach {
                keyframes[it.toInt()] = loadArmorstandKeyframe(frames.getConfigurationSection(it)!!)
            }
        }

        return@withContext ArmorstandLine(
            Function { p ->
                for (requirement in requirements) {
                    if (!requirement.check(p)) return@Function false
                }
                return@Function true
            },
            failLine,
            keyframes
        ) { p, line -> line }
    }

    private suspend fun loadArmorstandKeyframe(section: ConfigurationSection): ArmorstandLine.ArmorstandKeyframe = withContext(Dispatchers.IO) {
        val text = section.getString("text")!!
        val height = section.getDouble("height", 0.3)
        return@withContext ArmorstandLine.ArmorstandKeyframe(
            text.toAquatic(),
            height,
        )
    }

    suspend fun loadEmptyLine(section: ConfigurationSection): EmptyLine = withContext(Dispatchers.IO) {
        val height = section.getDouble("height", 0.3)
        val failLine = loadFailLine(section)
        val requirements = loadRequirements(section)
        return@withContext EmptyLine(
            Function { p ->
                for (requirement in requirements) {
                    if (!requirement.check(p)) return@Function false
                }
                return@Function true
            },
            failLine,
            height
        )
    }

    private suspend fun loadRequirements(section: ConfigurationSection): List<ConfiguredRequirement<Player>> = withContext(Dispatchers.IO) {
        return@withContext if (section.contains("conditions")) {
            RequirementSerializer.fromSections(section.getSectionList("conditions"))
        } else {
            arrayListOf()
        }
    }

    private suspend fun loadFailLine(section: ConfigurationSection): AquaticHologram.Line? = withContext(Dispatchers.IO) {
        return@withContext if (section.isConfigurationSection("fail-line")) {
            loadLine(section.getConfigurationSection("fail-line")!!)
        } else {
            null
        }
    }

}