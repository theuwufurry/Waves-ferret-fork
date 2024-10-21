package gg.aquatic.waves.module

import gg.aquatic.waves.Waves

interface WaveModule {

    val type: WaveModules

    suspend fun initialize(waves: Waves)
    fun disable(waves: Waves)

}