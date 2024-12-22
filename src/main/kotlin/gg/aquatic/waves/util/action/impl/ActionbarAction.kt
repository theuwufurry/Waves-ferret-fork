package gg.aquatic.waves.util.action.impl

import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.toMMComponent
import gg.aquatic.waves.util.toUser
import gg.aquatic.waves.util.updatePAPIPlaceholders
import org.bukkit.entity.Player

class ActionbarAction: AbstractAction<Player>() {

    override fun execute(binder: Player, args: Map<String, Any?>, textUpdater: (Player, String) -> String) {
        val message = (args["message"] as String).updatePAPIPlaceholders(binder)
        binder.toUser().sendMessage(textUpdater(binder,message).toMMComponent())
    }

    override val arguments: List<AquaticObjectArgument<*>> = listOf(PrimitiveObjectArgument("message", "", true))
}