package gg.aquatic.waves.profile.module.impl.economy

import gg.aquatic.waves.profile.module.ProfileModule
import gg.aquatic.waves.profile.module.ProfileModuleEntry
import java.sql.Connection
import java.util.*
import java.util.concurrent.CompletableFuture

class EconomyProfileModule(
): ProfileModule {
    override val id: String = "aquaticeconomy"

    val currencyDriver = CurrencyDriver().apply {
        this.initialize()
    }

    override fun loadEntry(uuid: UUID): CompletableFuture<out ProfileModuleEntry> {
        return currencyDriver.get(uuid)
    }

    override fun initialize(connection: Connection) {
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                "aquaticcurrency (" +
                "id INTEGER NOT NULL," +
                "currency_id NVARCHAR(64) NOT NULL," +
                "balance DECIMAL NOT NULL," +
                "PRIMARY KEY (id, currency_id)," +
                "FOREIGN KEY (id) REFERENCES aquaticprofiles(id)" +
                ")"
        ).use { preparedStatement ->
            preparedStatement.execute()
        }
    }
}