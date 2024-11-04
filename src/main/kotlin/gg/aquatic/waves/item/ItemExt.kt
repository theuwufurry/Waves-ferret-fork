package gg.aquatic.waves.item

import gg.aquatic.waves.registry.serializer.ItemSerializer
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

fun Material.toCustomItem(): AquaticItem {
    return ItemHandler.create(ItemStack(this))
}

suspend fun AquaticItem.Companion.loadFromYml(section: ConfigurationSection?): AquaticItem? {
    return ItemSerializer.fromSection(section)
}

fun ItemStack.fastMeta(): FastItemMeta {
    return FastItemMeta(this)
}

fun ItemStack.modifyFastMeta(block: FastItemMeta.() -> Unit) {
    val meta = fastMeta()
    block(meta)
    meta.apply()
}

fun ItemStack.modifyMeta(block: (ItemMeta) -> Unit) {
    val meta = itemMeta ?: return
    block(meta)
    itemMeta = meta
}