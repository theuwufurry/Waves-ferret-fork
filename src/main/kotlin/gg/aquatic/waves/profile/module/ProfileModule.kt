package gg.aquatic.waves.profile.module

import java.sql.Connection
import java.util.UUID
import java.util.concurrent.CompletableFuture

interface ProfileModule {
    val id: String

    fun loadEntry(uuid: UUID): CompletableFuture<out ProfileModuleEntry>

    fun initialize(connection: Connection)
}