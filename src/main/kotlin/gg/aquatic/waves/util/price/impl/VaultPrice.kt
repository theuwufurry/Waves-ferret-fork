//package gg.aquatic.waves.util.price.impl
//
//import gg.aquatic.waves.util.argument.AquaticObjectArgument
//import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
//import gg.aquatic.waves.util.price.AbstractPrice
////import net.milkbowl.vault.economy.Economy
//import org.bukkit.Bukkit
//import org.bukkit.entity.Player
//import org.bukkit.plugin.RegisteredServiceProvider
//
//class VaultPrice: AbstractPrice<Player>() {
//
//    companion object {
//        private val rsp = Bukkit.getServer().servicesManager.getRegistration(Economy::class.java) as RegisteredServiceProvider<Economy>
//        val econ = rsp.provider
//    }
//
//    override fun take(binder: Player, arguments: Map<String, Any?>) {
//        val amt = arguments["amount"] as Double
//        econ.withdrawPlayer(binder,amt)
//    }
//
//    override fun give(binder: Player, arguments: Map<String, Any?>) {
//        val amt = arguments["amount"] as Double
//        econ.depositPlayer(binder,amt)
//    }
//
//    override fun set(binder: Player, arguments: Map<String, Any?>) {
//        val amt = arguments["amount"] as Double
//        val toChange = amt- econ.getBalance(binder)
//        if (toChange > 0) {
//            econ.depositPlayer(binder, toChange)
//        } else if (toChange < 0) {
//            econ.withdrawPlayer(binder, -toChange)
//        }
//    }
//
//    override fun has(binder: Player, arguments: Map<String, Any?>): Boolean {
//        val amt = arguments["amount"] as Double
//        return econ.has(binder,amt)
//    }
//
//    override fun arguments(): List<AquaticObjectArgument<*>> {
//        return arrayListOf(
//            PrimitiveObjectArgument("amount",0,true)
//        )
//    }
//}