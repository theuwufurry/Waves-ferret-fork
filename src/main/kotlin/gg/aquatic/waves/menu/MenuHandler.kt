package gg.aquatic.waves.menu

import gg.aquatic.aquaticseries.lib.util.event
import gg.aquatic.waves.Waves
import gg.aquatic.waves.inventory.event.AsyncPacketInventoryInteractEvent
import gg.aquatic.waves.module.WaveModule
import gg.aquatic.waves.module.WaveModules

object MenuHandler: WaveModule {
    override val type: WaveModules = WaveModules.MENUS

    override fun initialize(waves: Waves) {
        event<AsyncPacketInventoryInteractEvent> {

        }
    }

    override fun disable(waves: Waves) {

    }
}