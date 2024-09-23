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
        EconomyProfileModule.currencyDriver.save(connection,this)
    }
}