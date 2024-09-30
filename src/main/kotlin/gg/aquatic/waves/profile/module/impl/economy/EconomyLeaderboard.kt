package gg.aquatic.waves.profile.module.impl.economy

import gg.aquatic.waves.economy.RegisteredCurrency
import java.util.*
import kotlin.collections.LinkedHashMap

class EconomyLeaderboard(
    val currency: RegisteredCurrency,
    var totalPlaces: Int
) {
    val leaderboard = LinkedHashMap<UUID,EconomyLeaderboardEntry>()

    class EconomyLeaderboardEntry(
        val rank: Int,
        val balance: Double,
        val username: String,
    )

}