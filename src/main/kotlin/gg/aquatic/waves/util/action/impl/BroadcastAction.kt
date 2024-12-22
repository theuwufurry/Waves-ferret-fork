package gg.aquatic.waves.util.action.impl

import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.broadcast
import gg.aquatic.waves.util.toMMComponent
import gg.aquatic.waves.util.updatePAPIPlaceholders
import org.bukkit.entity.Player

class BroadcastAction: AbstractAction<Player>() {

    override fun execute(binder: Player, args: Map<String, Any?>, textUpdater: (Player, String) -> String) {
        val messages = if (args["message"] != null) listOf(args["message"] as String) else args["messages"] as List<String>
        for (message in messages) {
            textUpdater(binder,message.updatePAPIPlaceholders(binder)).toMMComponent().broadcast()
        }
    }

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("message", "", false),
        PrimitiveObjectArgument("messages", mutableListOf<String>(), false)
    )
}