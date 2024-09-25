package gg.aquatic.waves.economy

import gg.aquatic.waves.profile.module.impl.economy.aquaticEconomy
import gg.aquatic.waves.profile.toAquaticPlayer
import org.bukkit.entity.Player

class CustomCurrency(
    val id: String,
    val limit: Double,
    val maxLeaderboardCache: Int,
) {
    fun getBalance(player: Player, registeredCurrency: RegisteredCurrency): Double {
        val aPlayer = player.toAquaticPlayer() ?: return 0.0
        return aPlayer.aquaticEconomy().balance(registeredCurrency)
    }

    fun give(player: Player, amount: Double, registeredCurrency: RegisteredCurrency) {
        val aPlayer = player.toAquaticPlayer() ?: return
        aPlayer.aquaticEconomy().give(registeredCurrency, amount)
    }

    fun has(player: Player, amount: Double, registeredCurrency: RegisteredCurrency): Boolean {
        val aPlayer = player.toAquaticPlayer() ?: return false
        return aPlayer.aquaticEconomy().has(registeredCurrency, amount)
    }

    fun set(player: Player, amount: Double, registeredCurrency: RegisteredCurrency) {
        val aPlayer = player.toAquaticPlayer() ?: return
        aPlayer.aquaticEconomy().set(registeredCurrency, amount)
    }

    fun take(player: Player, amount: Double, registeredCurrency: RegisteredCurrency) {
        val aPlayer = player.toAquaticPlayer() ?: return
        aPlayer.aquaticEconomy().take(registeredCurrency, amount)
    }
}