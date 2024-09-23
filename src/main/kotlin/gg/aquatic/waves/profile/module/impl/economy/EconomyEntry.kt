package gg.aquatic.waves.profile.module.impl.economy

import gg.aquatic.waves.economy.RegisteredCurrency
import gg.aquatic.waves.profile.AquaticPlayer
import gg.aquatic.waves.profile.module.ProfileModuleEntry
import java.sql.Connection
import java.util.*

class EconomyEntry(
    player: AquaticPlayer,
) : ProfileModuleEntry(player) {

    val balance = HashMap<RegisteredCurrency, Pair<Double,Double>>()

    override fun save(connection: Connection) {

        val newValues = balance.mapValues { (currency, pair) ->
            (pair.first to pair.first)
        }
        connection.prepareStatement("replace into aquaticcurrency values (?, ?, ?)").use { preparedStatement ->
            for ((currency, pair) in balance) {
                val (balance, previous) = pair
                if (balance == previous) {
                    continue
                }
                preparedStatement.setInt(1, aquaticPlayer.index)
                preparedStatement.setInt(2, currency.index)
                preparedStatement.setDouble(3, balance)
                preparedStatement.addBatch()
            }
            preparedStatement.executeBatch()
        }
        balance += newValues
    }
}