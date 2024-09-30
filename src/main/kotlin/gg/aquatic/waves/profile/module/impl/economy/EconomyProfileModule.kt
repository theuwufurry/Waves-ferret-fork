package gg.aquatic.waves.profile.module.impl.economy

import gg.aquatic.aquaticseries.lib.util.mapPair
import gg.aquatic.waves.Waves
import gg.aquatic.waves.economy.CustomCurrency
import gg.aquatic.waves.economy.RegisteredCurrency
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.profile.AquaticPlayer
import gg.aquatic.waves.profile.ProfilesModule
import gg.aquatic.waves.profile.module.ProfileModule
import gg.aquatic.waves.profile.module.ProfileModuleEntry
import gg.aquatic.waves.registry.WavesRegistry
import java.sql.Connection
import java.util.concurrent.CompletableFuture

object EconomyProfileModule : ProfileModule {
    override val id: String = "aquaticeconomy"

    val currencyDriver = CurrencyDriver().apply {
        //initialize(this)
    }

    val leaderboards = HashMap<RegisteredCurrency, EconomyLeaderboard>()

    override fun loadEntry(aquaticPlayer: AquaticPlayer): CompletableFuture<out ProfileModuleEntry> {
        return currencyDriver.get(aquaticPlayer)
    }

    fun initializeEconomy(currency: CustomCurrency): CompletableFuture<RegisteredCurrency> {
        val future = CompletableFuture<RegisteredCurrency>()
        CompletableFuture.runAsync {
            var id: Int? = null
            currencyDriver.driver.executeQuery("SELECT * FROM aquaticcurrency_type WHERE currency_id = ?",
                {
                    setString(1, currency.id)
                },
                {
                    if (next()) {
                        id = getInt("id")
                        val registeredCurrency = RegisteredCurrency(currency, id!!)
                        future.complete(registeredCurrency)
                    }
                }
            )

            if (id != null) {
                return@runAsync
            }
            currencyDriver.driver.useConnection {
                prepareStatement("INSERT INTO aquaticcurrency_type (currency_id) VALUES (?)").use { preparedStatement ->
                    preparedStatement.setString(1, currency.id)
                    preparedStatement.execute()
                    val rs = preparedStatement.generatedKeys
                    rs.next()
                    id = rs.getInt(1)
                    val registeredCurrency = RegisteredCurrency(currency, id!!)
                    future.complete(registeredCurrency)
                }
            }
        }.exceptionally {
            it.printStackTrace()
            return@exceptionally null
        }
        return future
    }

    override fun initialize(connection: Connection) {
        connection.prepareStatement(
            "CREATE TABLE IF NOT EXISTS " +
                    "aquaticcurrency_type (" +
                    "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "currency_id NVARCHAR(64) NOT NULL UNIQUE" +
                    ")"
        ).use { preparedStatement ->
            preparedStatement.execute()
        }
        connection.prepareStatement(
            "CREATE TABLE IF NOT EXISTS " +
                    "aquaticcurrency (" +
                    "id INTEGER NOT NULL," +
                    "currency_id INTEGER NOT NULL," +
                    "balance DECIMAL NOT NULL," +
                    "PRIMARY KEY (id, currency_id)," +
                    "FOREIGN KEY (id) REFERENCES aquaticprofiles(id)," +
                    "FOREIGN KEY (currency_id) REFERENCES aquaticcurrency_type(id)" +
                    ")"
        ).use { preparedStatement ->
            preparedStatement.execute()
        }
    }

}

fun AquaticPlayer.aquaticEconomy(): EconomyEntry {
    if (!this.entries.containsKey("aquaticeconomy")) {
        val places = HashMap<RegisteredCurrency, Int>()
        for ((currency, leaderboard) in EconomyProfileModule.leaderboards) {
            places += currency to leaderboard.totalPlaces
        }
        val entry = EconomyEntry(this, places)
        this.entries += "aquaticeconomy" to entry
        return entry
    }
    return this.entries["aquaticeconomy"] as EconomyEntry
}