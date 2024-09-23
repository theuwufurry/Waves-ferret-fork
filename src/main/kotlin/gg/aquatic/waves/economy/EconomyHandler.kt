package gg.aquatic.waves.economy

import gg.aquatic.waves.Waves
import gg.aquatic.waves.profile.module.impl.economy.EconomyProfileModule
import gg.aquatic.waves.registry.WavesRegistry
import java.util.concurrent.CompletableFuture

object EconomyHandler {

    fun register(currency: CustomCurrency): CompletableFuture<RegisteredCurrency> {
        //WavesRegistry.ECONOMY += currency.id to currency
        return EconomyProfileModule.initializeEconomy(currency).thenApply {
            WavesRegistry.INDEX_TO_CURRENCY += it.index to it
            WavesRegistry.ECONOMY += it.id to it
            it
        }
    }

}