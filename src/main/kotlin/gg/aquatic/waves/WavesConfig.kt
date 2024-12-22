package gg.aquatic.waves

import gg.aquatic.waves.data.DataDriver
import gg.aquatic.waves.sync.SyncSettings

class WavesConfig(
    val profilesDriver: DataDriver,
    val syncSettings: SyncSettings
) {
}