package gg.aquatic.waves.profile.module.impl.economy

import gg.aquatic.aquaticseries.lib.data.DataDriver
import gg.aquatic.aquaticseries.lib.economy.VirtualCurrency
import gg.aquatic.aquaticseries.lib.util.toBytes
import gg.aquatic.aquaticseries.lib.util.toUUID
import gg.aquatic.waves.Waves
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.profile.ProfilesModule
import java.util.*
import java.util.concurrent.CompletableFuture

class CurrencyDriver(
) {

    val driver: DataDriver = (Waves.INSTANCE.modules[WaveModules.PROFILES] as ProfilesModule).driver

    fun get(uuid: UUID): CompletableFuture<EconomyEntry> {
        val future = CompletableFuture<EconomyEntry>()
        CompletableFuture.runAsync {
            val rs = driver.executeQuery("SELECT * FROM aquaticcurrency WHERE uuid = ?") {
                setBytes(1, uuid.toBytes())
            }

            val economyPlayer = EconomyEntry(uuid)
            while(rs.next()) {
                val currencyId = rs.getString("currency_id")
                val balance = rs.getDouble("balance")
                economyPlayer.balance[currencyId] = balance
            }
            future.complete(economyPlayer)
        }
        return future
    }

    fun get(uuid: UUID, currency: VirtualCurrency): CompletableFuture<Double> {
        val future = CompletableFuture<Double>()
        CompletableFuture.runAsync {
            val rs = driver.executeQuery("SELECT balance FROM aquaticcurrency WHERE uuid = ? AND currency_id = ?") {
                setBytes(1, uuid.toBytes())
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

    fun set(uuid: UUID, currency: VirtualCurrency, amount: Double): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            driver.execute("replace into aquaticcurrency values (?, ?, ?)") {
                setBytes(1, uuid.toBytes())
                setString(2, currency.id)
                setDouble(3, amount)
            }
        }
    }

    fun set(vararg economyPlayers: EconomyEntry): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            driver.executeBatch("replace into aquaticcurrency values (?, ?, ?)") {
                for (economyPlayer in economyPlayers) {
                    for ((id, balance) in economyPlayer.balance) {
                        setBytes(1, economyPlayer.uuid.toBytes())
                        setString(2, id)
                        setDouble(3, balance)
                        addBatch()
                    }
                }
            }
        }
    }

    fun initialize(): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            driver.execute("" +
                    "CREATE TABLE " +
                    "aquaticcurrency (" +
                    "uuid BINARY(16) NOT NULL," +
                    "currency_id NVARCHAR(64) NOT NULL," +
                    "balance DECIMAL NOT NULL," +
                    "PRIMARY KEY (uuid, currency_id)" +
                    ")"
            ) {
            }
        }
    }
}