package gg.aquatic.waves.profile.event

import gg.aquatic.waves.profile.AquaticPlayer
import gg.aquatic.waves.util.event.AquaticEvent

class AsyncProfileLoadEvent(
    val profile: AquaticPlayer
): AquaticEvent(true) {

}