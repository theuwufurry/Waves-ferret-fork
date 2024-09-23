package gg.aquatic.waves.profile

import gg.aquatic.aquaticseries.lib.data.DataDriver
import gg.aquatic.aquaticseries.lib.util.event
import gg.aquatic.aquaticseries.lib.util.toBytes
import gg.aquatic.waves.Waves
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.profile.module.ProfileModule
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
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
                        "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                        "uuid BLOB(16) NOT NULL," +
                        "username NVARCHAR(64) NOT NULL" +
                        ")"
            ) {
            }
        }
    }

    val cache = HashMap<UUID, AquaticPlayer>()
    val playersSaving = HashSet<UUID>()
    val playersLoading = HashSet<UUID>()
    val modules = HashMap<String, ProfileModule>()

    override fun initialize(waves: Waves) {
        event<PlayerJoinEvent>(ignoredCancelled = true) {
            if (playersLoading.contains(it.player.uniqueId)) {
                return@event
            }
            playersLoading += it.player.uniqueId
            getOrCreate(it.player).thenAccept { player ->
                cache[player.uuid] = player
                playersLoading -= it.player.uniqueId
            }
        }
        event<PlayerQuitEvent>(ignoredCancelled = true) {
            val aPlayer = cache[it.player.uniqueId] ?: return@event
            playersSaving += it.player.uniqueId
            save(aPlayer).thenRun {
                playersSaving -= it.player.uniqueId
            }
            cache.remove(it.player.uniqueId)
        }
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

    fun save(vararg players: AquaticPlayer): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            driver.useConnection {
                for (player in players) {
                    if (player.updated) {
                        prepareStatement("UPDATE aquaticprofiles SET username = ? WHERE id = ?").use { preparedStatement ->
                            preparedStatement.setString(1, player.username)
                            preparedStatement.setInt(2, player.index)
                            preparedStatement.execute()
                        }
                    }
                    try {
                        for (value in player.entries.values) {
                            value.save(this)
                        }
                    } catch (ex: Exception) {
                        rollback()
                        ex.printStackTrace()
                    }
                }
            }
        }
    }

    fun getOrCreate(player: Player): CompletableFuture<AquaticPlayer> {
        return getOrCreate(player.uniqueId, player.name)
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
                val player = AquaticPlayer(rs.getInt("id"), uuid, rs.getString("username"))
                if (player.username != username) {
                    player.username = username
                    player.updated = true
                }

                for (value in modules.values) {
                    val entry = value.loadEntry(player).join()
                    player.entries[value.id] = entry
                }
                future.complete(player)
            } else {
                driver.preparedStatement("INSERT INTO aquaticprofiles (uuid, username) VALUES (?, ?)") {
                    setBytes(1, uuid.toBytes())
                    setString(2, username)
                    executeUpdate()
                    val keys = generatedKeys
                    keys.next()
                    val id = keys.getInt(1)

                    val player = AquaticPlayer(id, uuid, username)
                    player.updated = true
                    future.complete(player)
                }
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
                val player = AquaticPlayer(rs.getInt("id"), uuid, rs.getString("username"))
                cache[uuid] = player
                future.complete(Optional.of(player))
            } else {
                future.complete(Optional.empty())
            }
        }
        return future
    }

}