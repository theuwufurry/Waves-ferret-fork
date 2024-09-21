package gg.aquatic.waves.module

import gg.aquatic.waves.Waves

interface WaveModule {

    val type: WaveModules

    fun initialize(waves: Waves)
    fun disable(waves: Waves)

}