package gg.aquatic.waves.util.audience

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class GlobalAudience: AquaticAudience {
    override val uuids: Collection<UUID>
        get() {
            return Bukkit.getOnlinePlayers().map { it.uniqueId }
        }

    override fun canBeApplied(player: Player): Boolean {
        return true
    }
}