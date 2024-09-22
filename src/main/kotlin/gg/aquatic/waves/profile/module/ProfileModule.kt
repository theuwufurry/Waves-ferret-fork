package gg.aquatic.waves.profile.module

import gg.aquatic.waves.profile.AquaticPlayer
import java.sql.Connection
import java.util.concurrent.CompletableFuture

interface ProfileModule {
    val id: String

    fun loadEntry(player: AquaticPlayer): CompletableFuture<out ProfileModuleEntry>

    fun initialize(connection: Connection)
}