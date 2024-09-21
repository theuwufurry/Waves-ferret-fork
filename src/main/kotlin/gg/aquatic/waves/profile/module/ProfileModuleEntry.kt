package gg.aquatic.waves.profile.module

import java.sql.Connection
import java.util.UUID

abstract class ProfileModuleEntry(
    val uuid: UUID
) {

   abstract fun save(connection: Connection)

}