package gg.aquatic.waves.economy

import gg.aquatic.aquaticseries.lib.economy.Currency
import org.bukkit.entity.Player

class RegisteredCurrency(
    val currency: CustomCurrency,
    val index: Int
) : Currency {
    override val id: String = currency.id

    override fun getBalance(player: Player): Double {
        return currency.getBalance(player, this)
    }

    override fun give(player: Player, amount: Double) {
        currency.give(player, amount, this)
    }

    override fun has(player: Player, amount: Double): Boolean {
        return currency.has(player, amount, this)
    }

    override fun set(player: Player, amount: Double) {
        currency.set(player, amount, this)
    }

    override fun take(player: Player, amount: Double) {
        currency.take(player, amount, this)
    }
}