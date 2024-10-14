package gg.aquatic.waves.registry

import gg.aquatic.aquaticseries.lib.AquaticSeriesLib
import gg.aquatic.aquaticseries.lib.item2.AquaticItem
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

val NAMESPACE_KEY = NamespacedKey(AquaticSeriesLib.INSTANCE.plugin, "Custom_Item_Registry")

fun AquaticItem.register(namespace: String, id: String): Boolean {
    val registryId = registryId()
    val item = getUnmodifiedItem()
    if (registryId != null) return false
    val meta = item.itemMeta ?: return false
    meta.persistentDataContainer.set(NAMESPACE_KEY, PersistentDataType.STRING, "$namespace:$id")
    item.itemMeta = meta
    WavesRegistry.ITEM["$namespace:$id"] = this
    return true
}

fun AquaticItem.registryId(): String? {
    val meta = getUnmodifiedItem().itemMeta
    val pdc = meta?.persistentDataContainer ?: return null
    return pdc.get(NAMESPACE_KEY, PersistentDataType.STRING)
}

fun WavesRegistry.getItem(id: String): AquaticItem? {
    return ITEM[id]
}

fun WavesRegistry.getItem(itemStack: ItemStack): AquaticItem? {
    val pdc = itemStack.itemMeta?.persistentDataContainer ?: return null
    val namespacedKey = NAMESPACE_KEY
    if (!pdc.has(namespacedKey, PersistentDataType.STRING)) return null
    val id = pdc.get(namespacedKey, PersistentDataType.STRING)
    return ITEM[id]
}