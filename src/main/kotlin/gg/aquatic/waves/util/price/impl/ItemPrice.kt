package gg.aquatic.waves.util.price.impl

import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.ItemObjectArgument
import gg.aquatic.waves.util.item.toCustomItem
import gg.aquatic.waves.util.price.AbstractPrice
import org.bukkit.Material
import org.bukkit.entity.Player

class ItemPrice : AbstractPrice<Player>() {
    override fun take(binder: Player, arguments: Map<String, Any?>) {
        val item = (arguments["item"]!! as AquaticItem).getItem()
        var amount = item.amount
        for (content in binder.inventory.contents) {
            content ?: continue
            if (!content.isSimilar(item)) continue

            if (content.amount >= amount) {
                content.amount -= amount
                break
            } else {
                amount -= content.amount
                content.amount = 0
            }
        }
    }

    override fun give(binder: Player, arguments: Map<String, Any?>) {
        val item = (arguments["item"]!! as AquaticItem).getItem()
        val toDrop = binder.inventory.addItem(item)
        for (value in toDrop.values) {
            binder.location.world!!.dropItem(binder.location, value)
        }
    }

    override fun set(binder: Player, arguments: Map<String, Any?>) {

    }

    override fun has(binder: Player, arguments: Map<String, Any?>): Boolean {
        val item = (arguments["item"]!! as AquaticItem).getItem()
        var amount = item.amount
        for (content in binder.inventory.contents) {
            content ?: continue
            if (!content.isSimilar(item)) continue

            if (content.amount >= amount) {
                return true
            } else {
                amount -= content.amount
            }
        }
        return false
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return arrayListOf(
            ItemObjectArgument("item", Material.STONE.toCustomItem(), true)
        )
    }
}