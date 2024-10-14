package gg.aquatic.waves.profile.module

import gg.aquatic.waves.profile.AquaticPlayer
import java.sql.Connection

interface ProfileModule {
    val id: String

    suspend fun loadEntry(player: AquaticPlayer): ProfileModuleEntry

    fun initialize(connection: Connection)
}