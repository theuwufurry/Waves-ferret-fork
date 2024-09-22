package gg.aquatic.waves.profile.module.impl.economy

import gg.aquatic.waves.profile.AquaticPlayer
import gg.aquatic.waves.profile.module.ProfileModule
import gg.aquatic.waves.profile.module.ProfileModuleEntry
import java.sql.Connection
import java.util.concurrent.CompletableFuture

class EconomyProfileModule(
): ProfileModule {
    override val id: String = "aquaticeconomy"

    val currencyDriver = CurrencyDriver().apply {
        //initialize(this)
    }

    override fun loadEntry(aquaticPlayer: AquaticPlayer): CompletableFuture<out ProfileModuleEntry> {
        return currencyDriver.get(aquaticPlayer)
    }

    override fun initialize(connection: Connection) {
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                "aquaticcurrency_type (" +
                "id INTEGER NOT NULL," +
                "currency_id NVARCHAR(64) NOT NULL"+
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