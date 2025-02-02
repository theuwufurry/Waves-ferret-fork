package gg.aquatic.waves.util.action.impl

import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.toJson
import gg.aquatic.waves.util.toMMComponent
import gg.aquatic.waves.util.updatePAPIPlaceholders
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.chat.ComponentSerializer
import org.bukkit.entity.Player

class ActionbarAction : AbstractAction<Player>() {

    override fun execute(binder: Player, args: ObjectArguments, textUpdater: (Player, String) -> String) {
        val message = ((args.string("message") { str -> textUpdater(binder, str) })!!).updatePAPIPlaceholders(binder)
        binder.spigot().sendMessage(
            ChatMessageType.ACTION_BAR,
            ComponentSerializer.parse(textUpdater(binder, message).toMMComponent().toJson()).firstOrNull() ?: return
        )
    }

    override val arguments: List<AquaticObjectArgument<*>> = listOf(PrimitiveObjectArgument("message", "", true))
}