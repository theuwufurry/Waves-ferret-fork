package gg.aquatic.waves.item

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.manager.server.ServerVersion
import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemCustomModelData
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemEnchantments
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentType
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack
import kotlin.jvm.optionals.getOrNull

class FastItemMeta(
    val itemStack: ItemStack
) {

    val nms = SpigotConversionUtil.fromBukkitItemStack(itemStack)

    var displayName: Component?
        get() {
            return nms.getComponent(ComponentTypes.CUSTOM_NAME).getOrNull()
        }
        set(value) {
            nms.setComponent(ComponentTypes.CUSTOM_NAME, value)
            //itemStack.itemMeta = SpigotConversionUtil.toBukkitItemStack(nms).itemMeta
        }

    var lore: List<Component>
        get() {
            return nms.getComponent(ComponentTypes.LORE).getOrNull()?.lines ?: emptyList()
        }
        set(value) {
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

    var modelData: Int?
        get() {
            return nms.getComponent(ComponentTypes.CUSTOM_MODEL_DATA).getOrNull()
        }
        set(value) {
            if (value == null) {
                if (PacketEvents.getAPI().serverManager.version.isOlderThan(ServerVersion.V_1_21_1)) {
                    nms.unsetComponent(ComponentTypes.CUSTOM_MODEL_DATA)
                } else {
                    nms.unsetComponent(ComponentTypes.CUSTOM_MODEL_DATA_LISTS)
                }
            } else {
                if (PacketEvents.getAPI().serverManager.version.isOlderThan(ServerVersion.V_1_21_1)) {
                    nms.setComponent(ComponentTypes.CUSTOM_MODEL_DATA, value)
                } else {
                    nms.setComponent(ComponentTypes.CUSTOM_MODEL_DATA_LISTS, ItemCustomModelData(value))
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