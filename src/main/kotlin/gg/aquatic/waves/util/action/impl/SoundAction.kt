package gg.aquatic.waves.util.action.impl

import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import org.bukkit.entity.Player

class SoundAction: AbstractAction<Player>() {
    override fun execute(binder: Player, args: Map<String, Any?>, textUpdater: (Player, String) -> String) {
        val sound = args["sound"] as String
        val volume = args["volume"] as Float
        val pitch = args["pitch"] as Float

        binder.playSound(binder.location, sound, volume, pitch)
    }

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("sound", "minecraft:ambient.basalt_deltas.additions", true),
        PrimitiveObjectArgument("volume", 1.0f, false),
        PrimitiveObjectArgument("pitch", 1.0f, false)
    )
}