package gg.aquatic.waves

import gg.aquatic.aquaticseries.lib.AquaticSeriesLib
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules
import org.bukkit.plugin.java.JavaPlugin

class Waves: JavaPlugin() {

    val modules = HashMap<WaveModules,WaveModule>()

    companion object {
        lateinit var INSTANCE: Waves
            private set
    }

    override fun onLoad() {
        INSTANCE = this
    }

    override fun onEnable() {
        AquaticSeriesLib.init(
            this,
            listOf()
            )
    }

    override fun onDisable() {

    }

    fun initializeModule(module: WaveModule) {
        module.initialize(this)
        modules += module.type to module
    }

}