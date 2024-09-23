package gg.aquatic.waves.profile.event

import gg.aquatic.aquaticseries.lib.util.AquaticEvent
import gg.aquatic.waves.profile.AquaticPlayer

class ProfileUnloadEvent(
    val profile: AquaticPlayer
): AquaticEvent() {
}