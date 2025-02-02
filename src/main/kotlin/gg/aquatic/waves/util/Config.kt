package gg.aquatic.waves.util

import gg.aquatic.waves.Waves
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.Configuration
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import java.io.File
import java.io.IOException
import kotlin.reflect.KProperty

class Config {
    private var file: File
    private var config: FileConfiguration? = null
    private var main: JavaPlugin

    constructor(path: String) {
        main = Waves.INSTANCE
        file = File(main.dataFolder, path)
    }

    constructor(file: File) {
        main = Waves.INSTANCE
        this.file = file
    }
    constructor(file: File, main: JavaPlugin) {
        this.main = main
        this.file = file
    }
    constructor(path: String, main: JavaPlugin) {
        this.main = main
        file = File(main.dataFolder, path)
    }

    fun load() {
        if (!file.exists()) {
            try {
                main.saveResource(file.name, false)
            } catch (var4: IllegalArgumentException) {
                try {
                    file.createNewFile()
                } catch (var3: IOException) {
                    var3.printStackTrace()
                }
            }
        }
        config = YamlConfiguration.loadConfiguration(file)
    }

    fun getConfiguration(): FileConfiguration? {
        if (config == null) {
            load()
        }
        return config
    }

    fun save() {
        try {
            config!!.save(file)
        } catch (var2: IOException) {
            var2.printStackTrace()
        }
    }

    fun getFile(): File {
        return file
    }
}

class ConfigDelegate(private val name: String, private val main: JavaPlugin) {

    private val config: Config by lazy {
        Config(name, main)
    }

    init {
        config.load()
    }

    operator fun getValue(config: Config, property: KProperty<*>): FileConfiguration {
        return config.getConfiguration()!!
    }
}