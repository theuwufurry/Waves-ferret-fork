package gg.aquatic.waves.util.audience

import gg.aquatic.waves.util.audience.AquaticAudience
import org.bukkit.entity.Player

abstract class MutableAquaticAudience: AquaticAudience {

    abstract fun add(player: Player)
    abstract fun remove(player: Player)
    abstract fun contains(player: Player): Boolean

}