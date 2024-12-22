package gg.aquatic.waves.economy

import gg.aquatic.waves.profile.module.impl.economy.EconomyProfileModule
import gg.aquatic.waves.registry.WavesRegistry

object EconomyHandler {

    fun register(currency: CustomCurrency): RegisteredCurrency {
        //WavesRegistry.ECONOMY += currency.id to currency
        val registeredCurrency = EconomyProfileModule.initializeEconomy(currency)
        WavesRegistry.INDEX_TO_CURRENCY += registeredCurrency.index to registeredCurrency
        WavesRegistry.ECONOMY += registeredCurrency.id to registeredCurrency
        return registeredCurrency
    }

}