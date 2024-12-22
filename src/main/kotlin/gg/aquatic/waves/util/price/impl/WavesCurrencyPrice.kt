package gg.aquatic.waves.util.price.impl

import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.registry.WavesRegistry
import gg.aquatic.waves.registry.getCurrency
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.price.AbstractPrice
import org.bukkit.entity.Player

class WavesCurrencyPrice: AbstractPrice<Player>() {
    override fun take(binder: Player, arguments: Map<String, Any?>) {
        val currency = arguments["currency"] as String
        val amount = arguments["amount"] as Double
        WavesRegistry.getCurrency(currency)!!.take(binder, amount)
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf(
            PrimitiveObjectArgument("currency","example", true),
            PrimitiveObjectArgument("amount",1.0, true)
        )
    }

    override fun set(binder: Player, arguments: Map<String, Any?>) {
        val currency = arguments["currency"] as String
        val amount = arguments["amount"] as Double
        WavesRegistry.getCurrency(currency)!!.set(binder, amount)
    }

    override fun has(binder: Player, arguments: Map<String, Any?>): Boolean {
        val currency = arguments["currency"] as String
        val amount = arguments["amount"] as Double
        return WavesRegistry.getCurrency(currency)!!.has(binder, amount)
    }

    override fun give(binder: Player, arguments: Map<String, Any?>) {
        val currency = arguments["currency"] as String
        val amount = arguments["amount"] as Double
        WavesRegistry.getCurrency(currency)!!.give(binder, amount)
    }
}