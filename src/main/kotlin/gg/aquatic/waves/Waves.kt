package gg.aquatic.waves

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import gg.aquatic.aquaticseries.lib.AquaticSeriesLib
import gg.aquatic.aquaticseries.lib.betterinventory2.InventoryHandler
import gg.aquatic.aquaticseries.lib.data.MySqlDriver
import gg.aquatic.aquaticseries.lib.data.SQLiteDriver
import gg.aquatic.aquaticseries.lib.interactable2.InteractableHandler
import gg.aquatic.aquaticseries.lib.packet.PacketHandler
import gg.aquatic.aquaticseries.lib.util.*
import gg.aquatic.waves.chunk.ChunkTracker
import gg.aquatic.waves.entity.EntityHandler
import gg.aquatic.waves.fake.FakeObjectHandler
import gg.aquatic.waves.fake.block.FakeEntity
import gg.aquatic.waves.item.ItemHandler
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.profile.ProfilesModule
import gg.aquatic.waves.sync.SyncHandler
import gg.aquatic.waves.sync.SyncSettings
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bukkit.Bukkit
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Waves : JavaPlugin() {

    val modules = hashMapOf(
        WaveModules.PROFILES to ProfilesModule,
        WaveModules.ITEMS to ItemHandler,
        WaveModules.ENTITIES to EntityHandler,
        WaveModules.FAKE_OBJECTS to FakeObjectHandler,
        WaveModules.CHUNK_TRACKER to ChunkTracker
    )
    lateinit var configValues: WavesConfig

    companion object {
        lateinit var INSTANCE: Waves
            private set

        fun getModule(type: WaveModules): WaveModule? {
            return INSTANCE.modules[type]
        }
    }

    override fun onLoad() {
        INSTANCE = this
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
        PacketEvents.getAPI().load()
        loadConfig()
    }

    override fun onEnable() {
        PacketEvents.getAPI().init()
        AquaticSeriesLib.init(
            this,
            listOf(
                PacketHandler,
                InventoryHandler,
                InteractableHandler
            )
        )
        for ((_, module) in modules) {
            module.initialize(this@Waves)
        }
        WavesInitializeEvent().call()


        event<AsyncPlayerChatEvent> {
            runSync {
                val fakeEntity = FakeEntity(EntityTypes.PIG,it.player.location, 30)
                fakeEntity.register()
                Bukkit.broadcastMessage("Spawned!")
            }
        }


    }

    override fun onDisable() {
        PacketEvents.getAPI().terminate()
    }

    fun loadConfig() {
        dataFolder.mkdirs()
        val config = Config("config.yml", this)
        config.load()

        val cfg = config.getConfiguration()!!
        val type = cfg.getString("databases.profiles.type", "SQLITE")!!
        val ip = cfg.getString("databases.profiles.ip", "")!!
        val port = cfg.getInt("databases.profiles.port", 3306)
        val userName = cfg.getString("databases.profiles.username", "")!!
        val password = cfg.getString("databases.profiles.password", "")!!
        val database = cfg.getString("databases.profiles.database", "")!!
        val maxPoolSize = cfg.getInt("databases.profiles.maxPoolSize", 10)
        val poolName = cfg.getString("databases.profiles.poolName", "Waves Hikari Pool")!!

        val driver = if (type.uppercase() == "SQLITE") {
            val file = File(dataFolder, "$database.db")
            file.createNewFile()
            SQLiteDriver(file)
        } else {
            MySqlDriver(ip, port, database, userName, password, maxPoolSize, poolName)
        }

        val syncEnabled = cfg.getBoolean("sync.enabled", false)
        val syncIP = cfg.getString("sync.ip", "localhost")!!
        val syncPort = cfg.getInt("sync.port", 8080)
        val syncPassword = cfg.getString("sync.protection-key", "<PASSWORD>")!!
        val syncServerId = cfg.getString("sync.server-id", "main")!!
        val syncSettings = SyncSettings(syncEnabled, syncIP, syncPort, syncPassword, syncServerId)

        configValues = WavesConfig(driver, syncSettings)
        if (syncEnabled) {
            SyncHandler.initializeClient(syncSettings)
        }
    }

}