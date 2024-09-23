package gg.aquatic.waves

import gg.aquatic.aquaticseries.lib.AquaticSeriesLib
import gg.aquatic.aquaticseries.lib.Config
import gg.aquatic.aquaticseries.lib.data.MySqlDriver
import gg.aquatic.aquaticseries.lib.data.SQLiteDriver
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Waves: JavaPlugin() {

    val modules = HashMap<WaveModules,WaveModule>()
    lateinit var configValues: WavesConfig

    companion object {
        lateinit var INSTANCE: Waves
            private set
    }

    override fun onLoad() {
        INSTANCE = this
        loadConfig()
    }

    override fun onEnable() {
        AquaticSeriesLib.init(
            this,
            listOf()
            )
    }

    override fun onDisable() {

    }

    fun loadConfig() {
        dataFolder.mkdirs()
        val config = Config("config.yml")
        config.load()

        val cfg = config.getConfiguration()!!
        val type = cfg.getString("databases.profiles.type","SQLITE")!!
        val ip = cfg.getString("databases.profiles.ip","")!!
        val port = cfg.getInt("databases.profiles.port",3306)
        val userName = cfg.getString("databases.profiles.username","")!!
        val password = cfg.getString("databases.profiles.password","")!!
        val database = cfg.getString("databases.profiles.database","")!!
        val maxPoolSize = cfg.getInt("databases.profiles.maxPoolSize",10)
        val poolName = cfg.getString("databases.profiles.poolName","Waves Hikari Pool")!!

        val driver = if (type.lowercase() == "SQLITE") {
            SQLiteDriver(File("$database.db"))
        } else {
            MySqlDriver(ip, port, userName, password, database, maxPoolSize, poolName)
        }
        configValues = WavesConfig(driver)
    }

    fun initializeModule(module: WaveModule) {
        module.initialize(this)
        modules += module.type to module
    }

}