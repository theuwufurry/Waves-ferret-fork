package gg.aquatic.waves.profile.module

import gg.aquatic.waves.profile.AquaticPlayer
import java.sql.Connection
import java.util.UUID

abstract class ProfileModuleEntry(
    val aquaticPlayer: AquaticPlayer
) {

   abstract fun save(connection: Connection)

}