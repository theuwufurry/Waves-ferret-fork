package gg.aquatic.waves.registry

import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.economy.Currency
import gg.aquatic.aquaticseries.lib.item2.AquaticItem
import gg.aquatic.aquaticseries.lib.price.AbstractPrice
import gg.aquatic.aquaticseries.lib.price.player.impl.ItemPrice
import gg.aquatic.aquaticseries.lib.price.player.impl.VaultPrice
import gg.aquatic.aquaticseries.lib.requirement.AbstractRequirement
import gg.aquatic.waves.economy.RegisteredCurrency
import org.bukkit.entity.Player

object WavesRegistry {

    val INDEX_TO_CURRENCY = HashMap<Int, RegisteredCurrency>()
    val ECONOMY = HashMap<String,Currency>()
    val ACTION = HashMap<Class<*>,MutableMap<String,AbstractAction<*>>>()
    val REQUIREMENT = HashMap<Class<*>,MutableMap<String,AbstractRequirement<*>>>()
    val PRICE = HashMap<Class<*>,MutableMap<String,AbstractPrice<*>>>().apply {
        val p = getOrPut(Player::class.java) { HashMap() }
        p["item"] = ItemPrice()
        p["vault"] = VaultPrice()
    }
    val ITEM = HashMap<String, AquaticItem>()

}