package gg.aquatic.waves.registry

import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.economy.Currency

object WavesRegistry {

    val ECONOMY = HashMap<String,Currency>()
    val ACTION = HashMap<Class<*>,MutableMap<String,AbstractAction<*>>>()



}