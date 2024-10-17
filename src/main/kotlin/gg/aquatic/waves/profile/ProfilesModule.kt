package gg.aquatic.waves.profile

import gg.aquatic.aquaticseries.lib.data.DataDriver
import gg.aquatic.aquaticseries.lib.logger.type.InfoLogger
import gg.aquatic.aquaticseries.lib.util.call
import gg.aquatic.aquaticseries.lib.util.event
import gg.aquatic.aquaticseries.lib.util.runSync
import gg.aquatic.aquaticseries.lib.util.toBytes
import gg.aquatic.waves.Waves
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.profile.event.ProfileLoadEvent
import gg.aquatic.waves.profile.event.ProfileUnloadEvent
import gg.aquatic.waves.profile.module.ProfileModule
import gg.aquatic.waves.sync.SyncHandler
import gg.aquatic.waves.sync.SyncedPlayer
import gg.aquatic.waves.util.await
import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class ProfilesModule(
    val driver: DataDriver
) : WaveModule {
    override val type: WaveModules = WaveModules.PROFILES

    constructor() : this(Waves.INSTANCE.configValues.profilesDriver)

    val cache = ConcurrentHashMap<UUID, AquaticPlayer>()
    val playersSaving = HashSet<UUID>()
    val playersLoading = HashSet<UUID>()
    val playersAwaiting = HashSet<UUID>()
    val modules = HashMap<String, ProfileModule>()

    override fun initialize(waves: Waves) {
        CompletableFuture.runAsync {
            driver.execute(
                "" +
                        "CREATE TABLE IF NOT EXISTS " +
                        "aquaticprofiles (" +
                        "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                        "uuid BINARY(16) NOT NULL UNIQUE," +
                        "username NVARCHAR(64) NOT NULL" +
                        ")"
            ) {
            }
        }.exceptionally {
            it.printStackTrace()
            null
        }

        event<PlayerJoinEvent>(ignoredCancelled = true) {
            if (playersLoading.contains(it.player.uniqueId)) {
                return@event
            }
            playersLoading += it.player.uniqueId
            Bukkit.getConsoleSender().sendMessage("Loading profile!")

            suspend fun loadPlayer(): AquaticPlayer {
                val player = getOrCreate(it.player)
                Bukkit.getConsoleSender().sendMessage("Profile Loaded!")
                runSync {
                    ProfileLoadEvent(player).call()
                }
                cache[player.uuid] = player
                playersLoading -= it.player.uniqueId
                return player
            }

            val syncSettings = Waves.INSTANCE.configValues.syncSettings
            if (syncSettings.enabled) {
                await(Dispatchers.IO) {
                    val player = SyncHandler.client.getPlayerCache(it.player.uniqueId)
                    if (player != null) {
                        if (player.server == null) {
                            loadPlayer()
                        } else {
                            playersAwaiting += it.player.uniqueId
                        }
                    } else {
                        val aquaticPlayer = loadPlayer()
                        val cachedPlayer = SyncedPlayer(aquaticPlayer.uuid, aquaticPlayer.username, syncSettings.serverId, HashMap())
                        SyncHandler.client.cachePlayer(cachedPlayer)
                    }
                }
            } else {
                await {
                    loadPlayer()
                }
            }


        }
        event<PlayerQuitEvent>(ignoredCancelled = true) {
            Bukkit.getConsoleSender().sendMessage("Unloading profile!")
            val aPlayer = cache[it.player.uniqueId] ?: return@event
            playersSaving += it.player.uniqueId
            ProfileUnloadEvent(aPlayer).call()
            suspend fun savePlayer() {
                save(aPlayer)
                playersSaving -= it.player.uniqueId
                cache.remove(it.player.uniqueId)
            }

            val syncSettings = Waves.INSTANCE.configValues.syncSettings
            if (syncSettings.enabled) {
                await(Dispatchers.IO) {
                    savePlayer()
                    val player = SyncHandler.client.getPlayerCache(it.player.uniqueId)
                    if (player != null) {
                        if (player.server == null) {
                            savePlayer()
                        } else {
                            playersAwaiting += it.player.uniqueId
                        }
                    }
                }
            } else {
                await {
                    savePlayer()
                }
            }
        }
    }

    override fun disable(waves: Waves) {
        await {
            save(*cache.values.toTypedArray())
            cache.clear()
        }
    }

    suspend fun registerModule(module: ProfileModule) = coroutineScope {
        if (modules.containsKey(module.id)) {
            return@coroutineScope
        }
        launch {
            modules[module.id] = module

            driver.useConnection {
                module.initialize(this)
            }
        }

    }

    suspend fun save(vararg players: AquaticPlayer) = withContext(Dispatchers.IO) {
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

    suspend fun getOrCreate(player: Player): AquaticPlayer {
        return getOrCreate(player.uniqueId, player.name)
    }

    suspend fun getOrCreate(uuid: UUID, username: String): AquaticPlayer = coroutineScope {
        if (cache.containsKey(uuid)) {
            return@coroutineScope cache[uuid]!!
        }

        val optionalPlayer = driver.executeQuery("SELECT * FROM aquaticprofiles WHERE uuid = ?",
            {
                setBytes(1, uuid.toBytes())
            },
            {
                if (next()) {
                    InfoLogger.send("Player was found in the database!")
                    val id = getInt("id")
                    val player = AquaticPlayer(id, uuid, getString("username"))
                    if (player.username != username) {
                        player.username = username
                        player.updated = true
                    }


                    return@executeQuery Optional.of(player)
                }
                Optional.empty<AquaticPlayer>()
            }
        )
        optionalPlayer.ifPresent {
            launch {
                for (value in modules.values) {
                    val entry = value.loadEntry(it)
                    it.entries[value.id] = entry
                }
            }
        }
        return@coroutineScope optionalPlayer.or {
            InfoLogger.send("Player was not found in the database!")
            driver.preparedStatement("INSERT INTO aquaticprofiles (uuid, username) VALUES (?, ?)") {
                setBytes(1, uuid.toBytes())
                setString(2, username)
                executeUpdate()
                val keys = generatedKeys
                keys.next()
                val id = keys.getInt(1)

                val player = AquaticPlayer(id, uuid, username)
                player.updated = true
                Optional.of(player)
            }
        }.get()
    }

    suspend fun get(uuid: UUID): Optional<AquaticPlayer> = coroutineScope {
        if (cache.containsKey(uuid)) {
            return@coroutineScope Optional.of(cache[uuid]!!)
        }
        return@coroutineScope driver.executeQuery("SELECT * FROM aquaticprofiles WHERE uuid = ?",
            {
                setBytes(1, uuid.toBytes())
            },
            {
                if (next()) {
                    val player = AquaticPlayer(getInt("id"), uuid, getString("username"))
                    cache[uuid] = player
                    Optional.of(player)
                } else {
                    Optional.of(cache[uuid]!!)
                }
            }
        )
    }

}