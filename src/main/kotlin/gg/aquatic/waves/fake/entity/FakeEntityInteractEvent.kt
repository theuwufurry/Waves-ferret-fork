package gg.aquatic.waves.fake.entity

import gg.aquatic.waves.util.event.AquaticEvent
import org.bukkit.entity.Player

class FakeEntityInteractEvent(
    val fakeEntity: FakeEntity,
    val player: Player,
    val isLeftClick: Boolean
): AquaticEvent() {
}