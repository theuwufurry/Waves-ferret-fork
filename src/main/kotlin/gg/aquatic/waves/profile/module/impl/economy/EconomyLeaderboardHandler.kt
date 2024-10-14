package gg.aquatic.waves.profile.module.impl.economy

import gg.aquatic.aquaticseries.lib.util.mapPair
import gg.aquatic.aquaticseries.lib.util.toUUID
import gg.aquatic.waves.Waves
import gg.aquatic.waves.economy.RegisteredCurrency
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.profile.AquaticPlayer
import gg.aquatic.waves.profile.ProfilesModule
import gg.aquatic.waves.registry.WavesRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

suspend fun EconomyProfileModule.refreshLeaderboard() = withContext(Dispatchers.IO) {
    for (value in WavesRegistry.INDEX_TO_CURRENCY.values) {
        refreshLeaderboard(value)
    }
}

suspend fun EconomyProfileModule.getLeaderboardPlaces(player: AquaticPlayer): HashMap<RegisteredCurrency, Int> =
    withContext(Dispatchers.IO) {
        val map = HashMap<RegisteredCurrency, Int>()
        currencyDriver.driver.preparedStatement(
            "SELECT * FROM (SELECT id, currency_id, RANK() OVER (PARTITION BY currency_id ORDER BY balance DESC) AS rank FROM aquaticcurrency) WHERE id = ?"
        ) {
            setInt(1, player.index)
            executeQuery().use {
                var i = 0
                while (it.next()) {
                    val rank = it.getInt("rank")
                    val currency = WavesRegistry.INDEX_TO_CURRENCY[it.getInt("currency_id")] ?: continue
                    map[currency] = rank
                    //player.aquaticEconomy().leaderboardPlaces += currency to rank
                    i++
                }
            }
        }
        map
    }

suspend fun EconomyProfileModule.refreshLeaderboard(
    currency: RegisteredCurrency
) = withContext(Dispatchers.IO) {
    val limit = currency.currency.maxLeaderboardCache
    val leaderboard = leaderboards.getOrPut(currency) { EconomyLeaderboard(currency, 0) }
    if (limit <= 0) {
        return@withContext
    }
    currencyDriver.driver.preparedStatement("SELECT COUNT(*) FROM aquaticcurrency WHERE currency_id = ?") {
        setInt(1, currency.index)
        executeQuery().use {
            if (it.next()) {
                val count = it.getInt(1)
                leaderboard.totalPlaces = count
            }
        }
    }

    currencyDriver.driver.preparedStatement(
        "SELECT uuid, balance, username FROM aquaticcurrency INNER JOIN aquaticprofiles ON aquaticprofiles.id = aquaticcurrency.id WHERE aquaticcurrency.currency_id = ? ORDER BY aquaticcurrency.balance DESC LIMIT ?"
    ) {
        setInt(1, currency.index)
        setInt(2, limit)

        executeQuery().use {
            var i = 0
            while (it.next()) {
                val uuid = it.getBytes("uuid").toUUID()
                val username = it.getString("username")
                val balance = it.getDouble("balance")
                leaderboard.leaderboard[uuid] = EconomyLeaderboard.EconomyLeaderboardEntry(i, balance, username)
                i++
            }
        }
    }

    val users =
        (Waves.getModule(WaveModules.PROFILES) as ProfilesModule).cache.values.mapPair { it.index to it }
    currencyDriver.driver.preparedStatement(
        "SELECT * FROM (SELECT id, RANK() OVER (ORDER BY balance DESC) AS rank FROM aquaticcurrency WHERE currency_id = ?) WHERE id IN (${
            users.values.joinToString(
                ","
            ) { "?" }
        })"
    ) {
        setInt(1, currency.index)
        users.values.forEachIndexed { index, aquaticPlayer ->
            setInt(index + 2, aquaticPlayer.index)
        }
        executeQuery().use {
            var i = 0
            while (it.next()) {
                val rank = it.getInt("rank")
                val user = users[it.getInt("id")]
                if (user != null) {
                    user.aquaticEconomy().leaderboardPlaces += currency to rank
                }
                i++
            }
        }
    }
}