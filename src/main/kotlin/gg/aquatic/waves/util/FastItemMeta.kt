package gg.aquatic.waves.util

import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore
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
            return nms.getComponent(ComponentTypes.ITEM_NAME).getOrNull()
        }
        set(value) {
            nms.setComponent(ComponentTypes.ITEM_NAME, value)
            itemStack.itemMeta = SpigotConversionUtil.toBukkitItemStack(nms).itemMeta
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

    fun apply() {
        itemStack.itemMeta = SpigotConversionUtil.toBukkitItemStack(nms).itemMeta
    }
}