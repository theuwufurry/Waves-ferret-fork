package gg.aquatic.waves.item

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.manager.server.ServerVersion
import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemCustomModelData
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemEnchantments
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentType
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound
import com.github.retrooper.packetevents.protocol.nbt.NBTInt
import com.github.retrooper.packetevents.protocol.nbt.NBTList
import com.github.retrooper.packetevents.protocol.nbt.NBTNumber
import com.github.retrooper.packetevents.protocol.nbt.NBTString
import com.github.retrooper.packetevents.protocol.nbt.NBTType
import gg.aquatic.waves.util.toJson
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import org.bukkit.inventory.ItemStack
import kotlin.jvm.optionals.getOrNull

class FastItemMeta(
    val itemStack: ItemStack
) {

    val nms = SpigotConversionUtil.fromBukkitItemStack(itemStack)

    var displayName: Component?
        get() {
            if (PacketEvents.getAPI().serverManager.version.isOlderThan(ServerVersion.V_1_21_1)) {
                val json = nms.orCreateTag?.getCompoundTagOrNull("display")?.getStringTagValueOrNull("Name") ?: return null
                return JSONComponentSerializer.json().deserialize(json)
            }
            return nms.getComponent(ComponentTypes.CUSTOM_NAME).getOrNull()
        }
        set(value) {
            if (PacketEvents.getAPI().serverManager.version.isOlderThan(ServerVersion.V_1_21_1)) {
                var displayTag = nms.orCreateTag.getCompoundTagOrNull("display")
                if (displayTag == null) {
                    displayTag = NBTCompound()
                    nms.orCreateTag.setTag("display", displayTag)
                }
                displayTag.removeTag("Name")
                value ?: return
                displayTag.setTag("Name", NBTString(value.toJson()))
                return
            }
            nms.setComponent(ComponentTypes.CUSTOM_NAME, value)
            //nms.setComponent(ComponentTypes.CUSTOM_NAME, value)
            //itemStack.itemMeta = SpigotConversionUtil.toBukkitItemStack(nms).itemMeta
        }

    var lore: List<Component>
        get() {
            if (PacketEvents.getAPI().serverManager.version.isOlderThan(ServerVersion.V_1_21_1)) {
                val jsons = nms.orCreateTag?.getCompoundTagOrNull("display")?.getStringListTagOrNull("Lore") ?: return emptyList()
                return jsons.tags.map { JSONComponentSerializer.json().deserialize(it.value) }
            }
            return nms.getComponent(ComponentTypes.LORE).getOrNull()?.lines ?: emptyList()
        }
        set(value) {
            if (PacketEvents.getAPI().serverManager.version.isOlderThan(ServerVersion.V_1_21_1)) {
                var displayTag = nms.orCreateTag.getCompoundTagOrNull("display")
                if (displayTag == null) {
                    displayTag = NBTCompound()
                    nms.orCreateTag.setTag("display", displayTag)
                }
                displayTag.removeTag("Lore")
                displayTag.setTag("Lore", NBTList(NBTType.STRING, value.map { NBTString(it.toJson())}))
                return
            }
            nms.setComponent(
                ComponentTypes.LORE, ItemLore(value)
            )
        }

    var enchantments: Map<EnchantmentType, Int>
        get() {
            return nms.getComponent(ComponentTypes.ENCHANTMENTS).getOrNull()?.enchantments ?: emptyMap()
        }
        set(value) {
            nms.setComponent(ComponentTypes.ENCHANTMENTS, ItemEnchantments(value, true))
        }

    var customModelData: ItemCustomModelData?
        get() {
            return nms.getComponent(ComponentTypes.CUSTOM_MODEL_DATA_LISTS).getOrNull()
        }
        set(value) {
            if (value == null) {
                nms.unsetComponent(ComponentTypes.CUSTOM_MODEL_DATA_LISTS)
                return
            }
            nms.setComponent(ComponentTypes.CUSTOM_MODEL_DATA_LISTS, value)
        }

    @Deprecated("Use customModelData instead")
    var modelData: Int?
        get() {
            return if (PacketEvents.getAPI().serverManager.version.isOlderThan(ServerVersion.V_1_20_5)) {
                return nms.orCreateTag?.getNumberTagOrNull("CustomModelData")?.asInt
            } else {
                customModelData?.legacyId
            }
        }
        set(value) {
            if (value == null) {
                if (PacketEvents.getAPI().serverManager.version.isOlderThan(ServerVersion.V_1_20_5)) {
                    nms.orCreateTag.removeTag("CustomModelData")
                } else {
                    customModelData = null
                }
            } else {
                if (PacketEvents.getAPI().serverManager.version.isOlderThan(ServerVersion.V_1_20_5)) {
                    nms.orCreateTag.setTag("CustomModelData",NBTInt(value))
                } else {
                    customModelData = ItemCustomModelData(value)
                }
            }
        }

    fun enchantments(enchantments: Map<EnchantmentType, Int>, showInTooltip: Boolean = true) {
        nms.setComponent(ComponentTypes.ENCHANTMENTS, ItemEnchantments(enchantments, showInTooltip))
    }

    fun enchantmentsByNames(enchantments: Map<String, Int>, showInTooltip: Boolean = true) {
        val map = hashMapOf<EnchantmentType, Int>()
        for ((name, level) in enchantments) {
            val enchantment = EnchantmentTypes.getByName(name) ?: continue
            map[enchantment] = level
        }
        enchantments(map, showInTooltip)
    }

    fun addEnchantment(enchantment: EnchantmentType, level: Int, showInTooltip: Boolean = true) {
        val previous = enchantments
        val map = mutableMapOf(enchantment to level)
        map.putAll(previous)
        nms.setComponent(ComponentTypes.ENCHANTMENTS, ItemEnchantments(map, showInTooltip))
    }

    fun removeEnchantment(enchantment: EnchantmentType, showInTooltip: Boolean = true) {
        val previous = enchantments
        val map = mutableMapOf<EnchantmentType, Int>()
        map.putAll(previous)
        map.remove(enchantment)
        nms.setComponent(ComponentTypes.ENCHANTMENTS, ItemEnchantments(map, showInTooltip))
    }

    fun apply() {
        itemStack.itemMeta = SpigotConversionUtil.toBukkitItemStack(nms).itemMeta
    }
}