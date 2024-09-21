package gg.aquatic.waves.profile

import gg.aquatic.waves.profile.module.ProfileModuleEntry
import java.util.UUID

class AquaticPlayer(
    val index: Int,
    val uuid: UUID,
    var username: String
) {

    var updated = false

    val entries = HashMap<String,ProfileModuleEntry>()

}