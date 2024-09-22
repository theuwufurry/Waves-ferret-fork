package gg.aquatic.waves.economy

import gg.aquatic.aquaticseries.lib.economy.Currency
import org.bukkit.entity.Player

class RegisteredCurrency(
    val currency: CustomCurrency,
    val index: Int
): Currency {
    override val id: String
        get() = TODO("Not yet implemented")

    override fun getBalance(player: Player): Double {
        TODO("Not yet implemented")
    }

    override fun give(player: Player, amount: Double) {
        TODO("Not yet implemented")
    }

    override fun has(player: Player, amount: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun set(player: Player, amount: Double) {
        TODO("Not yet implemented")
    }

    override fun take(player: Player, amount: Double) {
        TODO("Not yet implemented")
    }
}