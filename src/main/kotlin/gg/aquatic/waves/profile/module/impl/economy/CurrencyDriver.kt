package gg.aquatic.waves.profile.module.impl.economy

import gg.aquatic.aquaticseries.lib.data.DataDriver
import gg.aquatic.waves.Waves
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.profile.AquaticPlayer
import gg.aquatic.waves.profile.ProfilesModule
import gg.aquatic.waves.registry.WavesRegistry
import java.util.concurrent.CompletableFuture

class CurrencyDriver(
) {

    val driver: DataDriver = (Waves.INSTANCE.modules[WaveModules.PROFILES] as ProfilesModule).driver

    fun get(aquaticPlayer: AquaticPlayer): CompletableFuture<EconomyEntry> {
        val future = CompletableFuture<EconomyEntry>()
        CompletableFuture.runAsync {
            val rs = driver.executeQuery("SELECT * FROM aquaticcurrency WHERE id = ?") {
                setInt(1, aquaticPlayer.index)
            }

            val entry = EconomyEntry(aquaticPlayer)
            while(rs.next()) {
                val currencyId = rs.getInt("currency_id")
                val balance = rs.getDouble("balance")

                val currency = WavesRegistry.INDEX_TO_CURRENCY[currencyId] ?: continue
                entry.balance[currency] = balance to balance
            }
            future.complete(entry)
        }
        return future
    }

    /*
    fun get(index: Int, currency: VirtualCurrency): CompletableFuture<Double> {
        val future = CompletableFuture<Double>()
        CompletableFuture.runAsync {
            val rs = driver.executeQuery("SELECT balance FROM aquaticcurrency WHERE id = ? AND currency_id = ?") {
                setInt(1, index)
                setString(2, currency.id)
            }
            if (rs.next()) {
                future.complete(rs.getDouble("balance"))
            } else {
                future.complete(0.0)
            }
        }
        return future
    }
    fun getAll(): CompletableFuture<Map<UUID, EconomyEntry>> {
        val future = CompletableFuture<Map<UUID, EconomyEntry>>()
        CompletableFuture.runAsync {
            val rs = driver.executeQuery("SELECT * FROM aquaticcurrency") {}
            val map = HashMap<UUID, EconomyEntry>()
            while(rs.next()) {
                val uuid = rs.getBytes("uuid").toUUID()
                val currencyId = rs.getString("currency_id")
                val balance = rs.getDouble("balance")

                val player = map.getOrPut(uuid) { EconomyEntry(uuid) }
                player.balance[currencyId] = balance
            }
            future.complete(map)
        }
        return future
    }

    fun getAll(players: Set<UUID>): CompletableFuture<Map<UUID, EconomyEntry>> {
        val future = CompletableFuture<Map<UUID, EconomyEntry>>()
        CompletableFuture.runAsync {
            val rs = driver.executeQuery("SELECT * FROM aquaticcurrency WHERE uuid in (${players.joinToString { "?" }})") {
                players.forEachIndexed { index, uuid ->
                    setString(index + 1, uuid.toString())
                }
            }
            val map = HashMap<UUID, EconomyEntry>()
            while(rs.next()) {
                val uuid = rs.getBytes("uuid").toUUID()
                val currencyId = rs.getString("currency_id")
                val balance = rs.getDouble("balance")
                val player = map.getOrPut(uuid) { EconomyEntry(uuid) }
                player.balance[currencyId] = balance
            }
            future.complete(map)
        }
        return future
    }
     */

    /*
    fun set(vararg aquaticPlayers: AquaticPlayer): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            driver.executeBatch("replace into aquaticcurrency values (?, ?, ?)") {
                for (player in aquaticPlayers) {
                    val entry = player.entries["aquaticeconomy"] as? EconomyEntry ?: continue
                    for ((id, balance) in entry.balance) {
                        setInt(1, player.index)
                        setString(2, id)
                        setDouble(3, balance)
                        addBatch()
                    }
                }
            }
        }
    }
     */

    /*
    fun initialize(): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            driver.execute("" +
                    "CREATE TABLE " +
                    "aquaticcurrency (" +
                    "id INTEGER NOT NULL," +
                    "currency_id INT NOT NULL," +
                    "balance DECIMAL NOT NULL," +
                    "PRIMARY KEY (id, currency_id)," +
                    "FOREIGN KEY (id) REFERENCES aquaticprofiles(id)" +
                    ")"
            ) {
            }
        }
    }
     */
}