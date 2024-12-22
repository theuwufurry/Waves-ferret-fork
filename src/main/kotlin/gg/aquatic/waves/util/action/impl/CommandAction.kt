package gg.aquatic.waves.util.action.impl

import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.updatePAPIPlaceholders
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class CommandAction: AbstractAction<Player>() {

    override fun execute(binder: Player, args: Map<String, Any?>, textUpdater: (Player, String) -> String) {
        val command = args["command"]!!


        val commands = if (command is List<*>) {
            command.map { it.toString() }
        } else {
            listOf(command.toString())
        }

        for (cmd in commands) {
            Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                textUpdater(binder, cmd.updatePAPIPlaceholders(binder))
            )
        }
    }

    override val arguments: List<AquaticObjectArgument<*>> = listOf(PrimitiveObjectArgument("command", "", true))
}