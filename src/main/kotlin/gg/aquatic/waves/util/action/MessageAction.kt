package gg.aquatic.waves.util.action

import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import gg.aquatic.aquaticseries.lib.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.aquaticseries.lib.util.replace
import gg.aquatic.aquaticseries.lib.util.toAquatic
import gg.aquatic.aquaticseries.lib.util.updatePAPIPlaceholders
import org.bukkit.entity.Player
import java.util.function.BiFunction

class MessageAction : AbstractAction<Player>() {

    override fun run(player: Player, args: Map<String, Any?>, textUpdater: BiFunction<Player, String, String>) {

        val message = args["message"]!!
        val messages = if (message is List<*>) message.map { it.toString() } else listOf(message.toString())

        for (msg in messages) {
            msg.updatePAPIPlaceholders(player).toAquatic().replace(textUpdater, player).send(player)
        }
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf(
            PrimitiveObjectArgument("message", "", true),
        )
    }


}