package gg.aquatic.waves.profile

import gg.aquatic.aquaticseries.lib.data.DataDriver
import gg.aquatic.aquaticseries.lib.util.toBytes
import gg.aquatic.waves.Waves
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.profile.module.ProfileModule
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletableFuture

class ProfilesModule(
    val driver: DataDriver
) : WaveModule {
    override val type: WaveModules = WaveModules.PROFILES

    init {
        CompletableFuture.runAsync {
            driver.execute(
                "" +
                        "CREATE TABLE IF NOT EXISTS" +
                        "aquaticprofiles (" +
                        "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT" +
                        "uuid BLOB(16) NOT NULL," +
                        "username NVARCHAR(64) NOT NULL" +
                        ")"
            ) {
            }
        }
    }

    val cache = HashMap<UUID, AquaticPlayer>()
    val modules = HashMap<String, ProfileModule>()

    override fun initialize(waves: Waves) {

    }

    override fun disable(waves: Waves) {

    }

    fun registerModule(module: ProfileModule): CompletableFuture<Void> {
        modules[module.id] = module

        return CompletableFuture.runAsync {
            driver.useConnection {
                module.initialize(this)
            }
        }
    }

    fun save(player: AquaticPlayer): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            driver.useConnection {
                try {
                    autoCommit = false
                    for (value in player.entries.values) {
                        value.save(this)
                    }
                    commit()
                    autoCommit = true
                } catch (ex: Exception) {
                    rollback()
                    ex.printStackTrace()
                }
            }
        }
    }

    fun getOrCreate(player: AquaticPlayer): CompletableFuture<AquaticPlayer> {
        return getOrCreate(player.uuid, player.username)
    }

    fun getOrCreate(uuid: UUID, username: String): CompletableFuture<AquaticPlayer> {
        if (cache.containsKey(uuid)) {
            return CompletableFuture.completedFuture(cache[uuid])
        }
        val future = CompletableFuture<AquaticPlayer>()
        CompletableFuture.runAsync {
            val rs = driver.executeQuery("SELECT * FROM aquaticprofiles WHERE uuid = ?") {
                setBytes(1, uuid.toBytes())
            }
            if (rs.next()) {
                val player = AquaticPlayer(uuid, rs.getString("username"))
                cache[uuid] = player
                if (player.username != username) {
                    player.username = username
                    player.updated = true
                }
                future.complete(player)
            } else {
                val player = AquaticPlayer(uuid, username)
                player.updated = true
                cache[uuid] = player
                future.complete(player)
            }
        }
        return future
    }

    fun get(uuid: UUID): CompletableFuture<Optional<AquaticPlayer>> {
        if (cache.containsKey(uuid)) {
            return CompletableFuture.completedFuture(Optional.of(cache[uuid]!!))
        }
        val future = CompletableFuture<Optional<AquaticPlayer>>()
        CompletableFuture.runAsync {
            val rs = driver.executeQuery("SELECT * FROM aquaticprofiles WHERE uuid = ?") {
                setBytes(1, uuid.toBytes())
            }
            if (rs.next()) {
                val player = AquaticPlayer(uuid, rs.getString("username"))
                cache[uuid] = player
                future.complete(Optional.of(player))
            } else {
                future.complete(Optional.empty())
            }
        }
        return future
    }

}