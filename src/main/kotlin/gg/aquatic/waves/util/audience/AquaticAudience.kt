package gg.aquatic.waves.util.audience

import org.bukkit.entity.Player
import java.util.*

interface AquaticAudience {

    val uuids: Collection<UUID>
    fun canBeApplied(player: Player): Boolean

}