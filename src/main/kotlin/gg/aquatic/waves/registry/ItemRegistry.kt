package gg.aquatic.waves.registry

import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.item.ItemHandler
import gg.aquatic.waves.item.AquaticItemInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

fun AquaticItem.register(
    namespace: String,
    id: String
): Boolean {
    return register(
        namespace, id,
        registerInteraction = false
    )
}

fun AquaticItem.register(
    namespace: String,
    id: String,
    interactionHandler: (AquaticItemInteractEvent) -> Unit
): Boolean {
    return register(namespace, id, interactionHandler, true)
}

private fun AquaticItem.register(
    namespace: String, id: String,
    interactionHandler: (AquaticItemInteractEvent) -> Unit = {}, registerInteraction: Boolean
): Boolean {
    val registryId = registryId()
    val item = getUnmodifiedItem()
    if (registryId != null) return false
    val meta = item.itemMeta ?: return false
    meta.persistentDataContainer.set(ItemHandler.NAMESPACE_KEY, PersistentDataType.STRING, "$namespace:$id")
    item.itemMeta = meta
    WavesRegistry.ITEM["$namespace:$id"] = this

    if (registerInteraction) {
        ItemHandler.listenInteractions["$namespace:$id"] = interactionHandler
    }
    return true
}

fun AquaticItem.setInteractionHandler(interactionHandler: (AquaticItemInteractEvent) -> Unit): Boolean {
    val registryId = registryId() ?: return false
    ItemHandler.listenInteractions[registryId] = interactionHandler
    return true
}

fun AquaticItem.removeInteractionHandler(): Boolean {
    val registryId = registryId() ?: return false
    ItemHandler.listenInteractions.remove(registryId)
    return true
}

private fun AquaticItem.unregister(): Boolean {
    val registryId = registryId() ?: return false
    val item = getUnmodifiedItem()
    val meta = item.itemMeta ?: return false
    meta.persistentDataContainer.remove(ItemHandler.NAMESPACE_KEY)
    item.itemMeta = meta
    WavesRegistry.ITEM.remove(registryId)
    ItemHandler.listenInteractions.remove(registryId)
    return true
}

fun AquaticItem.registryId(): String? {
    val meta = getUnmodifiedItem().itemMeta
    val pdc = meta?.persistentDataContainer ?: return null
    return pdc.get(ItemHandler.NAMESPACE_KEY, PersistentDataType.STRING)
}

fun WavesRegistry.getItem(id: String): AquaticItem? {
    return ITEM[id]
}

fun WavesRegistry.getItem(itemStack: ItemStack): AquaticItem? {
    val pdc = itemStack.itemMeta?.persistentDataContainer ?: return null
    val namespacedKey = ItemHandler.NAMESPACE_KEY
    if (!pdc.has(namespacedKey, PersistentDataType.STRING)) return null
    val id = pdc.get(namespacedKey, PersistentDataType.STRING)
    return ITEM[id]
}

fun ItemStack.isAquaticItem(): AquaticItem? {
    val meta = itemMeta ?: return null
    val pdc = meta.persistentDataContainer
    val namespacedKey = ItemHandler.NAMESPACE_KEY
    if (!pdc.has(namespacedKey, PersistentDataType.STRING)) return null
    val id = pdc.get(namespacedKey, PersistentDataType.STRING)
    return WavesRegistry.ITEM[id]
}