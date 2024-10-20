package gg.aquatic.waves.profile.module.impl.economy

import gg.aquatic.waves.economy.RegisteredCurrency
import gg.aquatic.waves.profile.AquaticPlayer
import gg.aquatic.waves.profile.module.ProfileModuleEntry
import java.sql.Connection
import kotlin.collections.HashMap

class EconomyEntry(
    player: AquaticPlayer,
    val leaderboardPlaces: HashMap<RegisteredCurrency,Int>
) : ProfileModuleEntry(player) {

    val balance = HashMap<RegisteredCurrency, Pair<Double,Double>>()

    override fun save(connection: Connection) {
        CurrencyDriver.save(connection,this)
    }

    fun balance(registeredCurrency: RegisteredCurrency): Double {
        return balance.getOrDefault(registeredCurrency, Pair(0.0,0.0)).first
    }
    fun give(registeredCurrency: RegisteredCurrency, amount: Double): Double {
        val previous = balance.getOrDefault(registeredCurrency, Pair(0.0,0.0))
        val newBalance = previous.first + amount
        balance[registeredCurrency] = Pair(newBalance, previous.second)
        return newBalance
    }
    fun take(registeredCurrency: RegisteredCurrency, amount: Double): Double {
        val previous = balance.getOrDefault(registeredCurrency, Pair(0.0,0.0))
        val newBalance = previous.first - amount
        balance[registeredCurrency] = Pair(newBalance, previous.second)
        return newBalance
    }
    fun set(registeredCurrency: RegisteredCurrency, amount: Double) {
        val previous = balance.getOrDefault(registeredCurrency, Pair(0.0,0.0))
        balance[registeredCurrency] = Pair(amount, previous.second)
    }
    fun has(registeredCurrency: RegisteredCurrency, amount: Double): Boolean {
        return balance(registeredCurrency) >= amount
    }
}