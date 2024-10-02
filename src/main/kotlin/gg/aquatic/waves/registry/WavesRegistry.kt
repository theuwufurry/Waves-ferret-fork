package gg.aquatic.waves.registry

import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.economy.Currency
import gg.aquatic.aquaticseries.lib.item2.AquaticItem
import gg.aquatic.waves.economy.RegisteredCurrency

object WavesRegistry {

    val INDEX_TO_CURRENCY = HashMap<Int, RegisteredCurrency>()
    val ECONOMY = HashMap<String,Currency>()
    val ACTION = HashMap<Class<*>,MutableMap<String,AbstractAction<*>>>()
    val ITEM = HashMap<String, AquaticItem>()

}