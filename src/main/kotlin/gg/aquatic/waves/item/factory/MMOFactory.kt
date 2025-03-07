//package gg.aquatic.waves.item.factory
//
//import gg.aquatic.waves.item.ItemHandler
//import net.Indyuce.mmoitems.MMOItems
//import org.bukkit.inventory.ItemStack
//
//object MMOFactory: ItemHandler.Factory {
//    override fun create(id: String): ItemStack? {
//        val args = id.split(":")
//        return MMOItems.plugin.getItem(args[0], args[1])
//    }
//}