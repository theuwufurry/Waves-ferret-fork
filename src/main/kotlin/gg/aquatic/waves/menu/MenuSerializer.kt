package gg.aquatic.waves.menu

import gg.aquatic.aquaticseries.lib.betterinventory2.SlotSelection
import gg.aquatic.aquaticseries.lib.betterinventory2.action.ConfiguredActionsWithConditions
import gg.aquatic.aquaticseries.lib.util.getSectionList
import gg.aquatic.waves.inventory.ButtonType
import gg.aquatic.waves.inventory.InventoryType
import gg.aquatic.waves.menu.settings.AnimatedButtonSettings
import gg.aquatic.waves.menu.settings.ButtonSettings
import gg.aquatic.waves.menu.settings.IButtonSettings
import gg.aquatic.waves.menu.settings.PrivateMenuSettings
import gg.aquatic.waves.registry.serializer.InventorySerializer.loadActionsWithConditions
import gg.aquatic.waves.registry.serializer.ItemSerializer
import gg.aquatic.waves.registry.serializer.RequirementSerializer
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.ArrayList
import java.util.HashMap
import java.util.TreeMap

object MenuSerializer {

    fun loadPrivateInventory(section: ConfigurationSection): PrivateMenuSettings {
        val type = if (section.contains("size")) {
            val size = section.getInt("size",54)
            Bukkit.broadcastMessage("Size: $size")
            when(size) {
                54 -> InventoryType.GENERIC9X6
                45 -> InventoryType.GENERIC9X5
                36 -> InventoryType.GENERIC9X4
                27 -> InventoryType.GENERIC9X3
                18 -> InventoryType.GENERIC9X2
                9 -> InventoryType.GENERIC9X1
                else -> InventoryType.GENERIC9X6
            }
        } else InventoryType.valueOf(section.getString("type","GENERIC9X6")!!.uppercase())
        Bukkit.broadcastMessage("Type: $type")

        val title = section.getString("title") ?: ""
        val components = HashMap<String,IButtonSettings>()
        val componentsSection = section.getConfigurationSection("buttons")
        if (componentsSection != null) {
            for (componentId in componentsSection.getKeys(false)) {
                val componentSection = componentsSection.getConfigurationSection(componentId)
                if (componentSection != null) {
                    components[componentId] = loadButton(componentSection, componentId)
                }
            }
        }
        Bukkit.broadcastMessage("Components: ${components.size}")
        return PrivateMenuSettings(type, MiniMessage.miniMessage().deserialize(title), components)
    }

    fun loadButton(section: ConfigurationSection, id: String): IButtonSettings {
        val priority = section.getInt("priority")
        val updateEvery = section.getInt("update-every",10)
        val viewRequirements = RequirementSerializer.fromSections<Player>(section.getSectionList("view-requirements"))
        val clickActions = loadClickSettings(section.getSectionList("click-actions"))
        val failComponentSection = section.getConfigurationSection("fail-component")
        val failComponent = failComponentSection?.let { loadButton(it, id) }

        if (section.contains("frames")) { // Animated button
            val frames = TreeMap<Int,IButtonSettings>()
            val framesSection = section.getConfigurationSection("frames")
            if (framesSection != null) {
                for (frameId in framesSection.getKeys(false)) {
                    val frameSection = framesSection.getConfigurationSection(frameId)
                    if (frameSection != null) {
                        frames[frameId.toIntOrNull() ?: continue] = loadButton(frameSection, id)
                    }
                }
            }
            return AnimatedButtonSettings(id, frames, viewRequirements, clickActions, priority, updateEvery, failComponent)
        } else {
            val item = ItemSerializer.fromSection(section.getConfigurationSection("item"))
            val slots = loadSlotSelection(section.getStringList("slots")).slots
            return ButtonSettings(id, item,slots,viewRequirements,clickActions,priority,updateEvery,failComponent)
        }
    }

    fun loadClickSettings(sections: List<ConfigurationSection>): gg.aquatic.waves.menu.settings.ClickSettings {
        val map = HashMap<ButtonType, MutableList<ConfiguredActionsWithConditions>>()
        for (section in sections) {
            val actions = loadActionsWithConditions(section) ?: continue
            for (menuClickActionType in section.getStringList("types")
                .mapNotNull { ButtonType.valueOf(it.uppercase()) }) {
                val list = map.getOrPut(menuClickActionType) { ArrayList() }
                list.add(actions)
            }
        }
        return gg.aquatic.waves.menu.settings.ClickSettings(map)
    }

    fun loadSlotSelection(list: List<String>): SlotSelection {
        val slots = ArrayList<Int>()
        for (slot in list) {
            if (slot.contains("-")) {
                val range = slot.split("-")
                val start = range[0].toInt()
                val end = range[1].toInt()
                slots += start..end
            } else {
                slots += slot.toInt()
            }
        }
        return SlotSelection.of(slots)
    }

}