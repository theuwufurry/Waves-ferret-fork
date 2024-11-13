package gg.aquatic.waves.fake.entity

import gg.aquatic.aquaticseries.lib.util.AquaticEvent
import org.bukkit.entity.Player

class FakeEntityInteractEvent(
    val fakeEntity: FakeEntity,
    val player: Player,
    val isLeftClick: Boolean
): AquaticEvent() {
}