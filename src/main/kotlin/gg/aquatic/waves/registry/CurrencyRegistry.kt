package gg.aquatic.waves.registry

import gg.aquatic.aquaticseries.lib.economy.Currency
import gg.aquatic.waves.economy.CustomCurrency
import gg.aquatic.waves.economy.EconomyHandler
import gg.aquatic.waves.economy.RegisteredCurrency
import java.util.concurrent.CompletableFuture

fun WavesRegistry.registerCurrency(currency: Currency) {
    if (currency is RegisteredCurrency) return
    ECONOMY += currency.id to currency
}

fun WavesRegistry.getCurrency(id: String): Currency? {
    return ECONOMY[id]
}


fun WavesRegistry.registerCurrency(currency: CustomCurrency): CompletableFuture<RegisteredCurrency> {
    return EconomyHandler.register(currency)
}