package gg.aquatic.waves.economy

import gg.aquatic.waves.Waves
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules
import java.util.concurrent.CompletableFuture

object EconomyHandler: WaveModule {

    fun register(currency: CustomCurrency): CompletableFuture<RegisteredCurrency> {
        //WavesRegistry.ECONOMY += currency.id to currency

        val future = CompletableFuture<RegisteredCurrency>()

        // TODO: SQL REGISTRATION

        return future
    }

    override val type: WaveModules = WaveModules.ECONOMY

    override fun initialize(waves: Waves) {

    }

    override fun disable(waves: Waves) {

    }

}