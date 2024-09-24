package gg.aquatic.waves.profile.module.impl.economy

import gg.aquatic.waves.economy.CustomCurrency
import gg.aquatic.waves.economy.RegisteredCurrency
import gg.aquatic.waves.profile.AquaticPlayer
import gg.aquatic.waves.profile.module.ProfileModule
import gg.aquatic.waves.profile.module.ProfileModuleEntry
import java.sql.Connection
import java.util.concurrent.CompletableFuture

object EconomyProfileModule: ProfileModule {
    override val id: String = "aquaticeconomy"

    val currencyDriver = CurrencyDriver().apply {
        //initialize(this)
    }

    override fun loadEntry(aquaticPlayer: AquaticPlayer): CompletableFuture<out ProfileModuleEntry> {
        return currencyDriver.get(aquaticPlayer)
    }

    fun initializeEconomy(currency: CustomCurrency): CompletableFuture<RegisteredCurrency> {
        val future = CompletableFuture<RegisteredCurrency>()
        CompletableFuture.runAsync {
            val rs = currencyDriver.driver.executeQuery("SELECT * FROM aquaticcurrency_type WHERE currency_id = ?") {
                setString(1, currency.id)
            }

            if (rs.next()) {
                val id = rs.getInt("id")
                val registeredCurrency = RegisteredCurrency(currency, id)
                future.complete(registeredCurrency)
                return@runAsync
            }
            currencyDriver.driver.useConnection {
                prepareStatement("INSERT INTO aquaticcurrency_type (currency_id) VALUES (?)").use { preparedStatement ->
                    preparedStatement.setString(1, currency.id)
                    preparedStatement.execute()
                    val rs = preparedStatement.generatedKeys
                    rs.next()
                    val id = rs.getInt(1)
                    val registeredCurrency = RegisteredCurrency(currency, id)
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
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                "aquaticcurrency_type (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "currency_id NVARCHAR(64) NOT NULL UNIQUE"+
                ")"
        ).use { preparedStatement ->
            preparedStatement.execute()
        }
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS " +
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
    return this.entries.getOrPut("aquaticeconomy") { EconomyEntry(this) } as EconomyEntry
}